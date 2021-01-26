package br.ufc.mdcc.cmu.pmslib.mqttbroker;

import android.content.Context;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.File;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class MQTTProtocol {

    private Context context = null;
    public static MQTTProtocol instance = null;
    private String tmpDir = System.getProperty("java.io.tmpdir");
    private MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
    private final String TAG = getClass().getSimpleName();
    private MqttAndroidClient client;

    private MQTTProtocol(Context context){
        this.context = context;
    }

    public static MQTTProtocol getInstance(Context context){
        if(instance == null)
            instance = new MQTTProtocol(context);
        return instance;
    }

    public void publish(File file){
        //this.publish(null, "192.168.1.67", file);
    }

    private String getURI(String host){
        return "tcp://"+host+":1883";
    }

    public void publish(String topic, String host, File file){
        Log.d(TAG, ">> Here! "+file.toString());
        host = "localhost";

        MqttClient client;
        try {
            byte bytes[] = FileUtils.readFileToByteArray(file);
            client = new MqttClient(getURI(host), TAG + "-publish" + getIntRandom(), this.dataStore);
            client.connect();
            MqttMessage msg = new MqttMessage(bytes);
            msg.setQos(0);
            client.publish(topic, msg);
            client.disconnect();
        } catch (MqttException | IOException e) {
            e.printStackTrace();
        }

    }

    private int getIntRandom(){
        Random random = new Random();
        return random.nextInt(100)+1;
    }

    public Boolean subscribe(String topic, MqttCallback mqttCallback) throws MqttException, InterruptedException {
        String host = "localhost";

        //int[] qoss = new int[topic.length];
        //for (int i = 0; i < topic.length; i++) {
            //topic[i] = topic[i];
            //qoss[i] = 0;
        //}

        //Generates a random number to compose customer ID
        int randNumber = getIntRandom();
        MqttClient client = new MqttClient("tcp://"+host+":1883", TAG+"subscribe"+randNumber, this.dataStore);

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
            Log.d(TAG, ">> Mensagem recebida: "+message.toString()+" no tópico "+topic);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    }

}
