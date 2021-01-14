package br.ufc.mdcc.cmu.pmslib.brokermqtt;

import android.content.Context;

import br.ufc.mdcc.cmu.pmslib.exception.BrokerMQTTException;

/**
 * Created by makleyston on 14/01/2021
 */

public final class BrokerMQTTAdapterTechnology implements BrokerMQTTAdapterInterface {

    private Context context = null;
    private BrokerMQTTAdapterInterface brokerAdapter = null;
    private static BrokerMQTTAdapterTechnology brokerTechnology = null;

    public static BrokerMQTTAdapterTechnology getBrokerTechnology(Context context) {
        if(brokerTechnology == null){
            brokerTechnology = new BrokerMQTTAdapterTechnology(context);
        }
        return brokerTechnology;
    }

    public void setBrokerAdapter(BrokerMQTTAdapterInterface brokerAdapter){
        this.brokerAdapter = brokerAdapter;
    }

    public BrokerMQTTAdapterInterface getBrokerAdapter(){
        return this.brokerAdapter;
    }

    private BrokerMQTTAdapterTechnology(Context context){
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
