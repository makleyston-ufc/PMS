package br.ufc.mdcc.cmu.demo;

import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.util.concurrent.CountDownLatch;

public class MqttSubscriber {

    MqttAndroidClient client;
    private String tmpDir = System.getProperty("java.io.tmpdir");
    private MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
    final String TAG = getClass().getSimpleName();
    MQTTListener mqttListener = null;

    public MqttSubscriber(MQTTListener mqttListener){
        this.mqttListener = mqttListener;
    }

    public Boolean subscribe(String topic, MqttCallback mqttCallback) throws MqttException, InterruptedException {
        String host = "localhost";

        MqttClient client = new MqttClient("tcp://"+host+":1883", TAG+"subscribe", this.dataStore);

        if(mqttCallback == null) {
            SubscriberClientCallback callback = new SubscriberClientCallback();
            client.setCallback(callback);
        }else{
            client.setCallback(mqttCallback);
        }
        client.connect();
        int qoss = 0;
        client.subscribe(topic, qoss);

        //If client connected
        return client.isConnected();
    }

    private class SubscriberClientCallback implements MqttCallback {
        private CountDownLatch m_latch = new CountDownLatch(1);

        void waitFinish() throws InterruptedException {
            m_latch.await();
        }

        @Override
        public void connectionLost(Throwable cause) {
            m_latch.countDown();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            //Log.d(TAG, ">> Received message: "+message.toString()+" on topic "+topic);
            mqttListener.receivedData(topic, message.toString());
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    }

    public abstract static class MQTTListener {
        public abstract void receivedData(String topic, String message);
    }

}
