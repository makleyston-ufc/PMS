package br.ufc.mdcc.cmu.pmslib.mqttbroker;

import android.content.Context;

import br.ufc.mdcc.cmu.pmslib.exception.MQTTBrokerException;

/**
 * Created by makleyston on 14/01/2021
 */

public final class MQTTBrokerAdapterTechnology implements MQTTBrokerAdapterInterface {

    private Context context = null;
    private MQTTBrokerAdapterInterface brokerAdapter = null;
    private static MQTTBrokerAdapterTechnology brokerTechnology = null;

    public static MQTTBrokerAdapterTechnology getBrokerTechnology(Context context) {
        if(brokerTechnology == null){
            brokerTechnology = new MQTTBrokerAdapterTechnology(context);
        }
        return brokerTechnology;
    }

    public void setBrokerAdapter(MQTTBrokerAdapterInterface brokerAdapter){
        this.brokerAdapter = brokerAdapter;
    }

    public MQTTBrokerAdapterInterface getBrokerAdapter(){
        return this.brokerAdapter;
    }

    private MQTTBrokerAdapterTechnology(Context context){
        this.context = context;
    }

    @Override
    public void start() throws MQTTBrokerException {
        this.brokerAdapter.start();
    }

    @Override
    public void stop() throws MQTTBrokerException {
        this.brokerAdapter.stop();
    }

    @Override
    public boolean isActive() {
        return this.brokerAdapter.isActive();
    }
}
