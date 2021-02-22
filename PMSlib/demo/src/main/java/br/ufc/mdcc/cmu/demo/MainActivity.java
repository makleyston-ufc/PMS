package br.ufc.mdcc.cmu.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttException;

import br.ufc.mdcc.cmu.pmslib.PMS;
import br.ufc.mdcc.cmu.pmslib.PMSInterface;
import br.ufc.mdcc.cmu.pmslib.exception.PMSException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewResult = (TextView) findViewById(R.id.textViewResult);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.button:
                startPMS();
                break;
        }
    }

    private void startPMS(){
        PMSInterface pms = PMS.getInstance(this);
        pms.addCEPResourceClass(LocalizationCEPResource.class);

        try {
            pms.start();
        } catch (PMSException e) {
            e.printStackTrace();
        }

        startMQTT();
    }

    private void startMQTT(){

        MqttSubscriber mqttSubscriber = new MqttSubscriber(new MqttSubscriber.MQTTListener() {
            @Override
            public void receivedData(String topic, String message) {
                final String topic2 = topic;
                final String message2 = message;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewResult.setText("Received message on topic: "+topic2+" \n"+message2);
                    }
                });
            }
        });
        try {
            mqttSubscriber.subscribe("#", null);
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
