package br.ufc.mdcc.cmu.pmslib;

import android.content.Context;

import br.ufc.mdcc.cmu.pmslib.mqttbroker.MQTTBrokerAdapterImpl;
import br.ufc.mdcc.cmu.pmslib.mqttbroker.MQTTBrokerAdapterInterface;
import br.ufc.mdcc.cmu.pmslib.mqttbroker.MQTTBrokerAdapterTechnology;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.IoTMiddlewareAdapterImpl;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.IoTMiddlewareAdapterInterface;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.IoTMiddlewareListenerImpl;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.IoTMiddlewareTechnology;

public class PMS implements PMSInterface {

    private Context context = null;

    /*Technologies*/
    private MQTTBrokerAdapterTechnology brokerMQTTTechnology = null;
    private IoTMiddlewareTechnology ioTMiddlewareTechnology = null;

    private static PMS instance = null;

    private PMS(Context context){
        this.context = context;
    }

    public static PMS getInstance(Context context){
        if(instance == null)
            instance = new PMS(context);
        return instance;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    private void init(Context context){
        //Init the ontology framework
        this.initIoTMiddleware(context);

        //Init the MQTT Broker
        this.initBrokerMQTTAdapter(context);

        //Init the IoT middleware
        this.initOntologyFramework();

        //Init the CEP
        this.initCEP();
    }

    private void initBrokerMQTTAdapter(Context context){
        MQTTBrokerAdapterInterface brokerMQTTAdapter = new MQTTBrokerAdapterImpl();
        brokerMQTTTechnology = MQTTBrokerAdapterTechnology.getBrokerTechnology(context);
        brokerMQTTTechnology.setBrokerAdapter(brokerMQTTAdapter);
    }

    private void initOntologyFramework(){

    }

    private void initIoTMiddleware(Context context){
        IoTMiddlewareAdapterInterface ioTMiddlewareAdapter = new IoTMiddlewareAdapterImpl();
        ioTMiddlewareTechnology = IoTMiddlewareTechnology.getInstance(context);
        ioTMiddlewareTechnology.setIoTMiddlewareAdapter(ioTMiddlewareAdapter);

        IoTMiddlewareListenerImpl ioTMiddlewareListener = new IoTMiddlewareListenerImpl(context);
        ioTMiddlewareTechnology.setListener(ioTMiddlewareListener);
    }

    private void initCEP(){

    }

}
