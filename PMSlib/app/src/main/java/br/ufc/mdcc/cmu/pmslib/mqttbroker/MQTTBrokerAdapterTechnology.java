package br.ufc.mdcc.cmu.pmslib.mqttbroker;

import android.content.Context;

import br.ufc.mdcc.cmu.pmslib.exception.BrokerMQTTException;

/**
 * Created by makleyston on 14/01/2021
 */

<<<<<<< HEAD:PMSlib/app/src/main/java/br/ufc/mdcc/cmu/pmslib/mqttbroker/MQTTBrokerAdapterTechnology.java
public final class MQTTBrokerAdapterTechnology implements MQTTBrokerAdapterInterface {
=======
public final class BrokerMQTTAdapterTechnology implements BrokerMQTTAdapterInterface {
>>>>>>> BrokerMQTT:PMSlib/app/src/main/java/br/ufc/mdcc/cmu/pmslib/brokermqtt/BrokerMQTTAdapterTechnology.java

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
    public void start() throws BrokerMQTTException {
        this.brokerAdapter.start();
    }

    @Override
    public void stop() throws BrokerMQTTException {
        this.brokerAdapter.stop();
    }

    @Override
    public boolean isActive() {
        return this.brokerAdapter.isActive();
    }
}
