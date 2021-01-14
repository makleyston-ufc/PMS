package br.ufc.mdcc.cmu.pmslib.brokermqtt;

import android.util.Log;

public class BrokerMQTTAdapterImpl implements BrokerMQTTAdapterInterface {
    boolean active = false;
    private final String TAG = getClass().getSimpleName();

    @Override
    public void start() {
        this.active = true;
        Log.d(TAG, "Initialized Broker MQTT");
    }

    @Override
    public void stop() {
        this.active = false;
        Log.d(TAG, "Finished Broker MQTT");
    }

    @Override
    public boolean isActive() {
        return this.active;
    }
}
