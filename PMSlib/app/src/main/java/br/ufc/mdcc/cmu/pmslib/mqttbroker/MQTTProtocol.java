package br.ufc.mdcc.cmu.pmslib.mqttbroker;

import android.content.Context;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.util.Random;

public class MQTTProtocol {

    private Context context = null;
    public static MQTTProtocol instance = null;
    private String tmpDir = System.getProperty("java.io.tmpdir");
    private MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
    private final String TAG = getClass().getSimpleName();

    private MQTTProtocol(Context context){
        this.context = context;
    }

    public static MQTTProtocol getInstance(Context context){
        if(instance == null)
            instance = new MQTTProtocol(context);
        return instance;
    }

    public void publish(String message){
        this.publish(null, null, message);
    }

    public void publish(String topic, String host, String message){
        MqttClient client;
        try {
            client = new MqttClient(host, TAG+"-publish-"+getIntRandom(), this.dataStore);
            client.connect();
            MqttMessage msg = new MqttMessage(message.getBytes());
            msg.setQos(0);
            client.publish(topic, msg);
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private int getIntRandom(){
        Random random = new Random();
        return random.nextInt(100)+1;
    }
}
