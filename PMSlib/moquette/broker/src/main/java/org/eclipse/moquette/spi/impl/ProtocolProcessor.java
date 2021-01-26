/*
 * Copyright (c) 2012-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package org.eclipse.moquette.spi.impl;

import android.util.Log;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import org.eclipse.moquette.proto.messages.AbstractMessage;
import org.eclipse.moquette.proto.messages.AbstractMessage.QOSType;
import org.eclipse.moquette.proto.messages.ConnAckMessage;
import org.eclipse.moquette.proto.messages.ConnectMessage;
import org.eclipse.moquette.proto.messages.DisconnectMessage;
import org.eclipse.moquette.proto.messages.PubAckMessage;
import org.eclipse.moquette.proto.messages.PubCompMessage;
import org.eclipse.moquette.proto.messages.PubRecMessage;
import org.eclipse.moquette.proto.messages.PubRelMessage;
import org.eclipse.moquette.proto.messages.PublishMessage;
import org.eclipse.moquette.proto.messages.SubAckMessage;
import org.eclipse.moquette.proto.messages.SubscribeMessage;
import org.eclipse.moquette.proto.messages.UnsubAckMessage;
import org.eclipse.moquette.proto.messages.UnsubscribeMessage;
import org.eclipse.moquette.server.ConnectionDescriptor;
import org.eclipse.moquette.server.Constants;
import org.eclipse.moquette.server.IAuthenticator;
import org.eclipse.moquette.server.ServerChannel;
import org.eclipse.moquette.spi.IMatchingCondition;
import org.eclipse.moquette.spi.IMessagesStore;
import org.eclipse.moquette.spi.ISessionsStore;
import org.eclipse.moquette.spi.impl.events.LostConnectionEvent;
import org.eclipse.moquette.spi.impl.events.MessagingEvent;
import org.eclipse.moquette.spi.impl.events.OutputMessagingEvent;
import org.eclipse.moquette.spi.impl.events.PubAckEvent;
import org.eclipse.moquette.spi.impl.events.PublishEvent;
import org.eclipse.moquette.spi.impl.subscriptions.Subscription;
import org.eclipse.moquette.spi.impl.subscriptions.SubscriptionsStore;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.eclipse.moquette.parser.netty.Utils.VERSION_3_1;
import static org.eclipse.moquette.parser.netty.Utils.VERSION_3_1_1;

/**
 * Class responsible to handle the logic of MQTT protocol it's the director of
 * the protocol execution. 
 * 
 * Used by the front facing class SimpleMessaging.
 * 
 * @author andrea
 */
class ProtocolProcessor implements EventHandler<ValueEvent> {
    
    static final class WillMessage {
        private final String topic;
        private final ByteBuffer payload;
        private final boolean retained;
        private final QOSType qos;

        public WillMessage(String topic, ByteBuffer payload, boolean retained, QOSType qos) {
            this.topic = topic;
            this.payload = payload;
            this.retained = retained;
            this.qos = qos;
        }

        public String getTopic() {
            return topic;
        }

        public ByteBuffer getPayload() {
            return payload;
        }

        public boolean isRetained() {
            return retained;
        }

        public QOSType getQos() {
            return qos;
        }
        
    }

    
    private Map<String, ConnectionDescriptor> m_clientIDs = new HashMap<>();
    private SubscriptionsStore subscriptions;
    private IMessagesStore m_messagesStore;
    private ISessionsStore m_sessionsStore;
    private IAuthenticator m_authenticator;
    //maps clientID to Will testament, if specified on CONNECT
    private Map<String, WillMessage> m_willStore = new HashMap<>();
    
    private ExecutorService m_executor;
    private RingBuffer<ValueEvent> m_ringBuffer;

    ProtocolProcessor() {}
    
    /**
     * @param subscriptions the subscription store where are stored all the existing
     *  clients subscriptions.
     * @param storageService the persistent store to use for save/load of messages
     *  for QoS1 and QoS2 handling.
     * @param sessionsStore the clients sessions store, used to persist subscriptions.
     * @param authenticator the authenticator used in connect messages
     */
    void init(SubscriptionsStore subscriptions, IMessagesStore storageService, 
            ISessionsStore sessionsStore,
            IAuthenticator authenticator) {
        //m_clientIDs = clientIDs;
        this.subscriptions = subscriptions;
        Log.d("Moquette", "subscription tree on init " + subscriptions.dumpTree());
        m_authenticator = authenticator;
        m_messagesStore = storageService;
        m_sessionsStore = sessionsStore;
        
        //init the output ringbuffer
        m_executor = Executors.newFixedThreadPool(1);

        Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, 1024 * 32, m_executor);
        disruptor.handleEventsWith(this);
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        m_ringBuffer = disruptor.getRingBuffer();
    }
    
    @MQTTMessage(message = ConnectMessage.class)
    void processConnect(ServerChannel session, ConnectMessage msg) {
        Log.d("Moquette", "processConnect for client " + msg.getClientID());
        if (msg.getProcotolVersion() != VERSION_3_1 && msg.getProcotolVersion() != VERSION_3_1_1) {
            ConnAckMessage badProto = new ConnAckMessage();
            badProto.setReturnCode(ConnAckMessage.UNNACEPTABLE_PROTOCOL_VERSION);
            Log.w("Moquette", "processConnect sent bad proto ConnAck");
            session.write(badProto);
            session.close(false);
            return;
        }

        if (msg.getClientID() == null || msg.getClientID().length() == 0) {
            ConnAckMessage okResp = new ConnAckMessage();
            okResp.setReturnCode(ConnAckMessage.IDENTIFIER_REJECTED);
            session.write(okResp);
            return;
        }

        //if an old client with the same ID already exists close its session.
        if (m_clientIDs.containsKey(msg.getClientID())) {
            Log.i("Moquette", "Found an existing connection with same client ID " + msg.getClientID() + ", forcing to close");
            //clean the subscriptions if the old used a cleanSession = true
            ServerChannel oldSession = m_clientIDs.get(msg.getClientID()).getSession();
            boolean cleanSession = (Boolean) oldSession.getAttribute(Constants.CLEAN_SESSION);
            if (cleanSession) {
                //cleanup topic subscriptions
                cleanSession(msg.getClientID());
            }

            oldSession.close(false);
            Log.d("Moquette", "Existing connection with same client ID " + msg.getClientID() + ", forced to close");
        }

        ConnectionDescriptor connDescr = new ConnectionDescriptor(msg.getClientID(), session, msg.isCleanSession());
        m_clientIDs.put(msg.getClientID(), connDescr);

        int keepAlive = msg.getKeepAlive();
        Log.d("Moquette", "Connect with keepAlive " + keepAlive + " s");
        session.setAttribute(Constants.KEEP_ALIVE, keepAlive);
        session.setAttribute(Constants.CLEAN_SESSION, msg.isCleanSession());
        //used to track the client in the subscription and publishing phases.
        session.setAttribute(Constants.ATTR_CLIENTID, msg.getClientID());

        session.setIdleTime(Math.round(keepAlive * 1.5f));

        //Handle will flag
        if (msg.isWillFlag()) {
            AbstractMessage.QOSType willQos = AbstractMessage.QOSType.values()[msg.getWillQos()];
            byte[] willPayload = msg.getWillMessage().getBytes();
            ByteBuffer bb = (ByteBuffer) ByteBuffer.allocate(willPayload.length).put(willPayload).flip();
            //save the will testment in the clientID store
            WillMessage will = new WillMessage(msg.getWillTopic(), bb, msg.isWillRetain(),willQos );
            m_willStore.put(msg.getClientID(), will);
        }

        //handle user authentication
        if (msg.isUserFlag()) {
            String pwd = null;
            if (msg.isPasswordFlag()) {
                pwd = msg.getPassword();
            }
            if (!m_authenticator.checkValid(msg.getUsername(), pwd)) {
                ConnAckMessage okResp = new ConnAckMessage();
                okResp.setReturnCode(ConnAckMessage.BAD_USERNAME_OR_PASSWORD);
                session.write(okResp);
                return;
            }
        }

        subscriptions.activate(msg.getClientID());

        //handle clean session flag
        if (msg.isCleanSession()) {
            //remove all prev subscriptions
            //cleanup topic subscriptions
            cleanSession(msg.getClientID());
        }

        ConnAckMessage okResp = new ConnAckMessage();
        okResp.setReturnCode(ConnAckMessage.CONNECTION_ACCEPTED);
        if (!msg.isCleanSession() && m_sessionsStore.contains(msg.getClientID())) {
            okResp.setSessionPresent(true);
        }
        Log.d("Moquette", "processConnect sent OK ConnAck");
        session.write(okResp);
        Log.i("Moquette", "Connected client ID " + msg.getClientID() + " with clean session " + msg.isCleanSession());

        Log.i("Moquette", "Create persistent session for clientID " + msg.getClientID());
        m_sessionsStore.addNewSubscription(Subscription.createEmptySubscription(msg.getClientID(), true), msg.getClientID()); //null means EmptySubscription
        
        if (!msg.isCleanSession()) {
            //force the republish of stored QoS1 and QoS2
            republishStored(msg.getClientID());
        }
    }
    
    private void republishStored(String clientID) {
        //Log.trace("republishStored invoked");
        List<PublishEvent> publishedEvents = m_messagesStore.retrievePersistedPublishes(clientID);
        if (publishedEvents == null) {
            Log.i("Moquette", "No stored messages for client " + clientID);
            return;
        }

        Log.i("Moquette", "republishing stored messages to client " + clientID);
        for (PublishEvent pubEvt : publishedEvents) {
            sendPublish(pubEvt.getClientID(), pubEvt.getTopic(), pubEvt.getQos(),
                   pubEvt.getMessage(), false, pubEvt.getMessageID());
        }
    }
    
    @MQTTMessage(message = PubAckMessage.class)
    void processPubAck(ServerChannel session, PubAckMessage msg) {
        String clientID = (String) session.getAttribute(Constants.ATTR_CLIENTID);
        int messageID = msg.getMessageID();
        //Remove the message from message store
        m_messagesStore.cleanPersistedPublishMessage(clientID, messageID);
    }
    
    private void cleanSession(String clientID) {
        Log.i("Moquette", "cleaning old saved subscriptions for client " + clientID);
        //remove from log all subscriptions
        m_sessionsStore.wipeSubscriptions(clientID);
        subscriptions.removeForClient(clientID);

        //remove also the messages stored of type QoS1/2
        m_messagesStore.cleanPersistedPublishes(clientID);
    }
    
    @MQTTMessage(message = PublishMessage.class)
    void processPublish(ServerChannel session, PublishMessage msg) {
        //LOG.trace("PUB --PUBLISH--> SRV processPublish invoked with " + msg);
        String clientID = (String) session.getAttribute(Constants.ATTR_CLIENTID);
        final String topic = msg.getTopicName();
        final AbstractMessage.QOSType qos = msg.getQos();
        final ByteBuffer message = msg.getPayload();
        boolean retain = msg.isRetainFlag();
        processPublish(clientID, topic, qos, message, retain, msg.getMessageID());
    }
        
    private void processPublish(String clientID, String topic, QOSType qos, ByteBuffer message, boolean retain, Integer messageID) { 
        Log.i("Moquette", "Publish received from clientID " + clientID + " on topic " + topic + " with QoS " + qos);

        String publishKey = null;
        if (qos == AbstractMessage.QOSType.LEAST_ONE) {
            //store the temporary message
            publishKey = String.format("%s%d", clientID, messageID);
            PublishEvent inFlightEvt = new PublishEvent(topic, qos, message, retain,
                        clientID, messageID);
            m_messagesStore.addInFlight(inFlightEvt, publishKey);
        }  else if (qos == AbstractMessage.QOSType.EXACTLY_ONCE) {
            publishKey = String.format("%s%d", clientID, messageID);
            //store the message in temp store
            PublishEvent qos2Persistent = new PublishEvent(topic, qos, message, retain,
                        clientID, messageID);
            m_messagesStore.persistQoS2Message(publishKey, qos2Persistent);
            sendPubRec(clientID, messageID);
        }

        //NB publish to subscribers for QoS 2 happen upon PUBREL from publisher
        if (qos != AbstractMessage.QOSType.EXACTLY_ONCE) {
            publish2Subscribers(topic, qos, message, retain, messageID);
        }

        if (qos == AbstractMessage.QOSType.LEAST_ONE) {
            if (publishKey == null) {
                throw new RuntimeException("Found a publish key null for QoS " + qos + " for message " + messageID);
            }
            m_messagesStore.cleanInFlight(publishKey);
            sendPubAck(new PubAckEvent(messageID, clientID));
            Log.d("Moquette", "replying with PubAck to MSG ID " + messageID);
        }

        if (retain) {
            if (qos == AbstractMessage.QOSType.MOST_ONE) {
                //QoS == 0 && retain => clean old retained 
                m_messagesStore.cleanRetained(topic);
            } else {
                m_messagesStore.storeRetained(topic, message, qos);
            }
        }
    }
    
    /**
     * Specialized version to publish will testament message.
     */
    private void processPublish(WillMessage will, String clientID) {
        final String topic = will.getTopic();
        final AbstractMessage.QOSType qos = will.getQos();
        final ByteBuffer message = will.getPayload();
        boolean retain = will.isRetained();
        processPublish(clientID, topic, qos, message, retain, null);
    }
    
    /**
     * Flood the subscribers with the message to notify. MessageID is optional and should only used for QoS 1 and 2
     * */
    private void publish2Subscribers(String topic, AbstractMessage.QOSType qos, ByteBuffer origMessage, boolean retain, Integer messageID) {
        Log.d("Moquette", "publish2Subscribers republishing to existing subscribers that matches the topic " + topic);
        /*if (LOG.isDebugEnabled()) {
            Log.d("Moquette", "content <{}>", DebugUtils.payload2Str(origMessage));
            Log.d("Moquette", "subscription tree " + subscriptions.dumpTree());
        }*/
        for (final Subscription sub : subscriptions.matches(topic)) {
            if (qos.ordinal() > sub.getRequestedQos().ordinal()) {
                qos = sub.getRequestedQos();
            }
            
            ByteBuffer message = origMessage.duplicate();
            Log.d("Moquette", "Broker republishing to client " + sub.getClientId() + " topic " + sub.getTopicFilter() + " qos " + qos + ", active " + sub.isActive());
            
            if (qos == AbstractMessage.QOSType.MOST_ONE && sub.isActive()) {
                //QoS 0
                sendPublish(sub.getClientId(), topic, qos, message, false);
            } else {
                //QoS 1 or 2
                //if the target subscription is not clean session and is not connected => store it
                if (!sub.isCleanSession() && !sub.isActive()) {
                    //clone the event with matching clientID
                    PublishEvent newPublishEvt = new PublishEvent(topic, qos, message, retain, sub.getClientId(), messageID != null ? messageID : 0);
                    m_messagesStore.storePublishForFuture(newPublishEvt);
                } else  {
                    //if QoS 2 then store it in temp memory
                    if (qos == AbstractMessage.QOSType.EXACTLY_ONCE) {
                        String publishKey = String.format("%s%d", sub.getClientId(), messageID);
                        PublishEvent newPublishEvt = new PublishEvent(topic, qos, message, retain, sub.getClientId(), messageID != null ? messageID : 0);
                        m_messagesStore.addInFlight(newPublishEvt, publishKey);
                    }
                    //publish
                    if (sub.isActive()) {
                        sendPublish(sub.getClientId(), topic, qos, message, false);
                    }
                }
            }
        }
    }
    
    private void sendPublish(String clientId, String topic, AbstractMessage.QOSType qos, ByteBuffer message, boolean retained) {
        //TODO pay attention to the message ID can't be 0 and it's the message sent to subscriber
        int messageID = 1;
        sendPublish(clientId, topic, qos, message, retained, messageID);
    }
    
    private void sendPublish(String clientId, String topic, AbstractMessage.QOSType qos, ByteBuffer message, boolean retained, int messageID) {
        Log.d("Moquette", "sendPublish invoked clientId " + clientId + " on topic " + topic + " QoS " + qos + " retained " + retained + " messageID " + messageID);
        PublishMessage pubMessage = new PublishMessage();
        pubMessage.setRetainFlag(retained);
        pubMessage.setTopicName(topic);
        pubMessage.setQos(qos);
        pubMessage.setPayload(message);

        Log.i("Moquette", "send publish message to " + clientId + " on topic " + topic);
        /*if (LOG.isDebugEnabled()) {
            Log.d("Moquette", "content <{}>", DebugUtils.payload2Str(message));
        }*/
        if (pubMessage.getQos() != AbstractMessage.QOSType.MOST_ONE) {
            pubMessage.setMessageID(messageID);
        }

        if (m_clientIDs == null) {
            throw new RuntimeException("Internal bad error, found m_clientIDs to null while it should be initialized, somewhere it's overwritten!!");
        }
        Log.d("Moquette", "clientIDs are " + m_clientIDs);
        if (m_clientIDs.get(clientId) == null) {
            throw new RuntimeException(String.format("Can't find a ConnectionDescriptor for client <%s> in cache <%s>", clientId, m_clientIDs));
        }
        Log.d("Moquette", "Session for clientId " + clientId + " is " + m_clientIDs.get(clientId).getSession());
//            m_clientIDs.get(clientId).getSession().write(pubMessage);
        disruptorPublish(new OutputMessagingEvent(m_clientIDs.get(clientId).getSession(), pubMessage));
    }
    
    private void sendPubRec(String clientID, int messageID) {
        //LOG.trace("PUB <--PUBREC-- SRV sendPubRec invoked for clientID {} with messageID " + clientID, messageID);
        PubRecMessage pubRecMessage = new PubRecMessage();
        pubRecMessage.setMessageID(messageID);

//        m_clientIDs.get(clientID).getSession().write(pubRecMessage);
        disruptorPublish(new OutputMessagingEvent(m_clientIDs.get(clientID).getSession(), pubRecMessage));
    }
    
    private void sendPubAck(PubAckEvent evt) {
        //LOG.trace("sendPubAck invoked");

        String clientId = evt.getClientID();

        PubAckMessage pubAckMessage = new PubAckMessage();
        pubAckMessage.setMessageID(evt.getMessageId());

        try {
            if (m_clientIDs == null) {
                throw new RuntimeException("Internal bad error, found m_clientIDs to null while it should be initialized, somewhere it's overwritten!!");
            }
            Log.d("Moquette", "clientIDs are " + m_clientIDs);
            if (m_clientIDs.get(clientId) == null) {
                throw new RuntimeException(String.format("Can't find a ConnectionDescriptor for client %s in cache %s", clientId, m_clientIDs));
            }
//            Log.d("Moquette", "Session for clientId " + clientId + " is " + m_clientIDs.get(clientId).getSession());
//            m_clientIDs.get(clientId).getSession().write(pubAckMessage);
            disruptorPublish(new OutputMessagingEvent(m_clientIDs.get(clientId).getSession(), pubAckMessage));
        }catch(Throwable t) {
            Log.e("Moquette", null, t);
        }
    }
    
    /**
     * Second phase of a publish QoS2 protocol, sent by publisher to the broker. Search the stored message and publish
     * to all interested subscribers.
     * */
    @MQTTMessage(message = PubRelMessage.class)
    void processPubRel(ServerChannel session, PubRelMessage msg) {
        String clientID = (String) session.getAttribute(Constants.ATTR_CLIENTID);
        int messageID = msg.getMessageID();
        Log.d("Moquette", "PUB --PUBREL--> SRV processPubRel invoked for clientID " + clientID + " ad messageID " + messageID);
        String publishKey = String.format("%s%d", clientID, messageID);
        PublishEvent evt = m_messagesStore.retrieveQoS2Message(publishKey);

        final String topic = evt.getTopic();
        final AbstractMessage.QOSType qos = evt.getQos();

        publish2Subscribers(topic, qos, evt.getMessage(), evt.isRetain(), evt.getMessageID());

        m_messagesStore.removeQoS2Message(publishKey);

        if (evt.isRetain()) {
            m_messagesStore.storeRetained(topic, evt.getMessage(), qos);
        }

        sendPubComp(clientID, messageID);
    }
    
    private void sendPubComp(String clientID, int messageID) {
        Log.d("Moquette", "PUB <--PUBCOMP-- SRV sendPubComp invoked for clientID " + clientID + " ad messageID " + messageID);
        PubCompMessage pubCompMessage = new PubCompMessage();
        pubCompMessage.setMessageID(messageID);

//        m_clientIDs.get(clientID).getSession().write(pubCompMessage);
        disruptorPublish(new OutputMessagingEvent(m_clientIDs.get(clientID).getSession(), pubCompMessage));
    }
    
    @MQTTMessage(message = PubRecMessage.class)
    void processPubRec(ServerChannel session, PubRecMessage msg) {
        String clientID = (String) session.getAttribute(Constants.ATTR_CLIENTID);
        int messageID = msg.getMessageID();
        //once received a PUBREC reply with a PUBREL(messageID)
        Log.d("Moquette", "\t\tSRV <--PUBREC-- SUB processPubRec invoked for clientID " + clientID + " ad messageID " + messageID);
        PubRelMessage pubRelMessage = new PubRelMessage();
        pubRelMessage.setMessageID(messageID);
        pubRelMessage.setQos(AbstractMessage.QOSType.LEAST_ONE);

//        m_clientIDs.get(clientID).getSession().write(pubRelMessage);
        disruptorPublish(new OutputMessagingEvent(m_clientIDs.get(clientID).getSession(), pubRelMessage));
    }
    
    @MQTTMessage(message = PubCompMessage.class)
    void processPubComp(ServerChannel session, PubCompMessage msg) {
        String clientID = (String) session.getAttribute(Constants.ATTR_CLIENTID);
        int messageID = msg.getMessageID();
        Log.d("Moquette", "\t\tSRV <--PUBCOMP-- SUB processPubComp invoked for clientID " + clientID + " ad messageID " + messageID);
        //once received the PUBCOMP then remove the message from the temp memory
        String publishKey = String.format("%s%d", clientID, messageID);
        m_messagesStore.cleanInFlight(publishKey);
    }
    
    @MQTTMessage(message = DisconnectMessage.class)
    void processDisconnect(ServerChannel session, DisconnectMessage msg) throws InterruptedException {
        String clientID = (String) session.getAttribute(Constants.ATTR_CLIENTID);
        boolean cleanSession = (Boolean) session.getAttribute(Constants.CLEAN_SESSION);
        if (cleanSession) {
            //cleanup topic subscriptions
            cleanSession(clientID);
        }
//        m_notifier.disconnect(evt.getSession());
        m_clientIDs.remove(clientID);
        session.close(true);

        //de-activate the subscriptions for this ClientID
        subscriptions.deactivate(clientID);
        //cleanup the will store
        m_willStore.remove(clientID);

        Log.i("Moquette", "Disconnected client " + clientID + " with clean session " + cleanSession);
    }
    
    void processConnectionLost(LostConnectionEvent evt) {
        String clientID = evt.clientID;
        if (m_clientIDs.containsKey(clientID)) {
            if (!m_clientIDs.get(clientID).getSession().equals(evt.session)) {
                Log.i("Moquette", "Received a lost connection with client " + clientID + " for a not matching session");
                return;
            }
        }

        //If already removed a disconnect message was already processed for this clientID
        if (m_clientIDs.remove(clientID) != null) {

            //de-activate the subscriptions for this ClientID
            subscriptions.deactivate(clientID);
            Log.i("Moquette", "Lost connection with client " + clientID);
        }
        //publish the Will message (if any) for the clientID
        if (m_willStore.containsKey(clientID)) {
            WillMessage will = m_willStore.get(clientID);
            processPublish(will, clientID);
            m_willStore.remove(clientID);
        }
    }
    
    /**
     * Remove the clientID from topic subscription, if not previously subscribed,
     * doesn't reply any error
     */
    @MQTTMessage(message = UnsubscribeMessage.class)
    void processUnsubscribe(ServerChannel session, UnsubscribeMessage msg) {
        List<String> topics = msg.topicFilters();
        int messageID = msg.getMessageID();
        String clientID = (String) session.getAttribute(Constants.ATTR_CLIENTID);
        Log.d("Moquette", "processUnsubscribe invoked, removing subscription on topics " + topics + ", for clientID " + clientID);

        for (String topic : topics) {
            subscriptions.removeSubscription(topic, clientID);
        }
        //ack the client
        UnsubAckMessage ackMessage = new UnsubAckMessage();
        ackMessage.setMessageID(messageID);

        Log.i("Moquette", "replying with UnsubAck to MSG ID " + messageID);
        session.write(ackMessage);
    }
    
    @MQTTMessage(message = SubscribeMessage.class)
    void processSubscribe(ServerChannel session, SubscribeMessage msg) {
        String clientID = (String) session.getAttribute(Constants.ATTR_CLIENTID);
        boolean cleanSession = (Boolean) session.getAttribute(Constants.CLEAN_SESSION);
        Log.d("Moquette", "processSubscribe invoked from client " + clientID + " with msgID " + msg.getMessageID());

        for (SubscribeMessage.Couple req : msg.subscriptions()) {
            AbstractMessage.QOSType qos = AbstractMessage.QOSType.values()[req.getQos()];
            Subscription newSubscription = new Subscription(clientID, req.getTopicFilter(), qos, cleanSession);
            subscribeSingleTopic(newSubscription, req.getTopicFilter());
        }

        //ack the client
        SubAckMessage ackMessage = new SubAckMessage();
        ackMessage.setMessageID(msg.getMessageID());

        //reply with requested qos
        for(SubscribeMessage.Couple req : msg.subscriptions()) {
            AbstractMessage.QOSType qos = AbstractMessage.QOSType.values()[req.getQos()];
            ackMessage.addType(qos);
        }
        
        Log.d("Moquette", "replying with SubAck to MSG ID " + msg.getMessageID());
        session.write(ackMessage);
    }
    
    private void subscribeSingleTopic(Subscription newSubscription, final String topic) {
        Log.i("Moquette", newSubscription.getClientId() + " subscribed to topic " + topic + " with QoS " + AbstractMessage.QOSType.formatQoS(newSubscription.getRequestedQos()));
        String clientID = newSubscription.getClientId();
        m_sessionsStore.addNewSubscription(newSubscription, clientID);
        subscriptions.add(newSubscription);

        //scans retained messages to be published to the new subscription
        Collection<IMessagesStore.StoredMessage> messages = m_messagesStore.searchMatching(new IMatchingCondition() {
            public boolean match(String key) {
                return  SubscriptionsStore.matchTopics(key, topic);
            }
        });

        for (IMessagesStore.StoredMessage storedMsg : messages) {
            //fire the as retained the message
            Log.d("Moquette", "send publish message for topic " + topic);
            sendPublish(newSubscription.getClientId(), storedMsg.getTopic(), storedMsg.getQos(), storedMsg.getPayload(), true);
        }
    }
    
    private void disruptorPublish(OutputMessagingEvent msgEvent) {
        Log.d("Moquette", "disruptorPublish publishing event on output " + msgEvent);
        long sequence = m_ringBuffer.next();
        ValueEvent event = m_ringBuffer.get(sequence);

        event.setEvent(msgEvent);
        
        m_ringBuffer.publish(sequence); 
    }

    public void onEvent(ValueEvent t, long l, boolean bln) throws Exception {
        MessagingEvent evt = t.getEvent();
        //It's always of type OutputMessagingEvent
        OutputMessagingEvent outEvent = (OutputMessagingEvent) evt;
        outEvent.getChannel().write(outEvent.getMessage());
    }

}
