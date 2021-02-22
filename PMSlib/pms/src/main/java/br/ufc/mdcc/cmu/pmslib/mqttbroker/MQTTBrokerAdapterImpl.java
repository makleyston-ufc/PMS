package br.ufc.mdcc.cmu.pmslib.mqttbroker;

import android.content.Context;
import android.util.Log;

import br.ufc.mdcc.cmu.pmslib.exception.MQTTBrokerException;

/**
 * Created by makleyston on 14/01/2021
 */

public class MQTTBrokerAdapterImpl extends MQTTBrokerAdapterInterface {

    private final String TAG = getClass().getSimpleName();

    MQTTBrokerAdapterImpl(Context context) {
        super(context);
    }

    @Override
    public void start() throws MQTTBrokerException {
        Log.d(TAG, "Initialized Broker MQTT");
        //TODO
    }

    @Override
    public void stop() throws MQTTBrokerException {
        Log.d(TAG, "Finished Broker MQTT");
        //TODO
    }

    @Override
    public boolean isActive() {
        return true;
        //TODO
    }

    @Override
    public void requestPermissions() {
        //TODO
    }

}
