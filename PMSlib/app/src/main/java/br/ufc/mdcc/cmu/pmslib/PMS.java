package br.ufc.mdcc.cmu.pmslib;

import android.content.Context;
import android.util.Log;

import br.ufc.mdcc.cmu.pmslib.exception.IoTMiddlewareException;
import br.ufc.mdcc.cmu.pmslib.exception.MQTTBrokerException;
import br.ufc.mdcc.cmu.pmslib.exception.OntologyFrameworkException;
import br.ufc.mdcc.cmu.pmslib.exception.PMSException;
import br.ufc.mdcc.cmu.pmslib.mqttbroker.MQTTBrokerAdapterImpl;
import br.ufc.mdcc.cmu.pmslib.mqttbroker.MQTTBrokerAdapterInterface;
import br.ufc.mdcc.cmu.pmslib.mqttbroker.MQTTBrokerTechnology;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.IoTMiddlewareAdapterImpl;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.IoTMiddlewareAdapterInterface;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.IoTMiddlewareListenerImpl;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.IoTMiddlewareTechnology;
import br.ufc.mdcc.cmu.pmslib.ontology.OntologyFrameworkAdapterImpl;
import br.ufc.mdcc.cmu.pmslib.ontology.OntologyFrameworkAdapterInterface;
import br.ufc.mdcc.cmu.pmslib.ontology.OntologyFrameworkTechnology;

public class PMS implements PMSInterface {

    private Context context = null;

    /*Technologies*/
    private MQTTBrokerTechnology mqttBrokerTechnology = null;
    private IoTMiddlewareTechnology ioTMiddlewareTechnology = null;
    private OntologyFrameworkTechnology ontologyFrameworkTechnology = null;

    private static PMS instance = null;
    private boolean active = false;
    private final String TAG = getClass().getSimpleName();

    private PMS(Context context){
        this.context = context;
    }

    public static PMS getInstance(Context context){
        if(instance == null)
            instance = new PMS(context);
        return instance;
    }

    @Override
    public void start() throws PMSException {
        if(!this.active) {
            init(this.context);
            this.active = true;
            Log.i(TAG, "PMS initialized successfully!");
        }else
            Log.i(TAG, "PMS already initialized!");
    }

    @Override
    public void stop() throws PMSException{

    }

    private void init(Context context){
        //Init the ontology framework
        this.initIoTMiddleware(context);

        //Init the MQTT Broker
        //this.initMQTTBrokerAdapter(context);

        //Init the IoT middleware
        //this.initOntologyFramework(context);

        //Init the CEP
        //this.initCEP();
    }

    private void end(){
        try {
            ioTMiddlewareTechnology.stop();
            ontologyFrameworkTechnology.stop();
            mqttBrokerTechnology.stop();
        } catch (IoTMiddlewareException e) {
            e.getMessage();
            e.printStackTrace();
        } catch (OntologyFrameworkException e) {
            e.getMessage();
            e.printStackTrace();
        } catch (MQTTBrokerException e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    private void initMQTTBrokerAdapter(Context context){
        MQTTBrokerAdapterInterface brokerMQTTAdapter = new MQTTBrokerAdapterImpl();
        mqttBrokerTechnology = MQTTBrokerTechnology.getBrokerTechnology(context);
        mqttBrokerTechnology.setBrokerAdapter(brokerMQTTAdapter);
    }

    private void initOntologyFramework(Context context){
        OntologyFrameworkAdapterInterface ontologyFrameworkAdapter = new OntologyFrameworkAdapterImpl();
        ontologyFrameworkTechnology = OntologyFrameworkTechnology.getInstance(context);
        ontologyFrameworkTechnology.setOntologyFrameworkAdapter(ontologyFrameworkAdapter);
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
