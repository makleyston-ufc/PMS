package br.ufc.mdcc.cmu.pmslib;

import android.content.Context;
import android.util.Log;

import com.hp.hpl.jena.ontology.OntModel;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import br.ufc.mdcc.cmu.pmslib.cep.CEPEventHandler;
import br.ufc.mdcc.cmu.pmslib.cep.CEPResource;
import br.ufc.mdcc.cmu.pmslib.cep.resources.GPSCEPResource;
import br.ufc.mdcc.cmu.pmslib.exception.CEPException;
import br.ufc.mdcc.cmu.pmslib.exception.IoTMiddlewareException;
import br.ufc.mdcc.cmu.pmslib.exception.MQTTBrokerException;
import br.ufc.mdcc.cmu.pmslib.exception.OntologyFrameworkException;
import br.ufc.mdcc.cmu.pmslib.exception.PMSException;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.googleawareness.IoTMiddlewareAdapterImpl;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.googleawareness.IoTMiddlewareListenerImpl;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.SensorInterface;
import br.ufc.mdcc.cmu.pmslib.mqttbroker.MQTTBrokerAdapterInterface;
import br.ufc.mdcc.cmu.pmslib.mqttbroker.MQTTBrokerTechnology;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.IoTMiddlewareAdapterInterface;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.IoTMiddlewareTechnology;
import br.ufc.mdcc.cmu.pmslib.mqttbroker.MQTTProtocol;
import br.ufc.mdcc.cmu.pmslib.mqttbroker.moquette.MQTTBrokerAdapterImpl;
import br.ufc.mdcc.cmu.pmslib.ontology.OntologyFrameworkAdapterInterface;
import br.ufc.mdcc.cmu.pmslib.ontology.OntologyFrameworkTechnology;
import br.ufc.mdcc.cmu.pmslib.ontology.jena.OntologyFrameworkAdapterImpl;
import br.ufc.mdcc.cmu.pmslib.ontology.jena.annotationFactory.GenericAnnotation;

/**
 * Created by makleyston on 14/01/2021
 */

public class PMS implements PMSInterface {

    private Context context = null;

    /*Technologies*/
    private MQTTBrokerTechnology mqttBrokerTechnology = null;
    private IoTMiddlewareTechnology ioTMiddlewareTechnology = null;
    private OntologyFrameworkTechnology ontologyFrameworkTechnology = null;
    private CEPEventHandler cepEventHandler = null;
    private MQTTProtocol mqttProtocol = null;

    /*Adapters*/
    private MQTTBrokerAdapterInterface MQTTBrokerAdapter = null;
    private OntologyFrameworkAdapterInterface ontologyFrameworkAdapter = null;
    private IoTMiddlewareAdapterInterface ioTMiddlewareAdapter = null;

    private List<Object> activeSensors = new ArrayList<>();

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

    public static PMS getInstance() throws PMSException{
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
        if(this.active){
            end();
            this.active = false;
            Log.i(TAG, "PMS stopped successfully!");
        }else
            Log.i(TAG, "PMS already stopped!");
    }

    @Override
    public void setIoTMiddlewareAdapter(IoTMiddlewareAdapterInterface ioTMiddlewareAdapter) {
        this.ioTMiddlewareAdapter = ioTMiddlewareAdapter;
    }

    @Override
    public void setMQTTBrokerAdapter(MQTTBrokerAdapterInterface mqttBrokerAdapter) {
        this.MQTTBrokerAdapter = mqttBrokerAdapter;
    }

    private void init(Context context){
        try {
            //Init the IoT middleware
            this.initIoTMiddleware(context);

            //Init the ontology framework
            this.initOntologyFramework(context);

            //Init the CEP
            this.initCEP(context);

            //Init the MQTT Broker
            this.initMQTTBrokerAdapter(context);

        } catch (IoTMiddlewareException | OntologyFrameworkException | MQTTBrokerException | CEPException e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    private void end(){
        try {
            ioTMiddlewareTechnology.stop();
            ontologyFrameworkTechnology.stop();
            mqttBrokerTechnology.stop();
            cepEventHandler.stop();
        } catch (IoTMiddlewareException | OntologyFrameworkException | MQTTBrokerException | CEPException e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    private void initMQTTBrokerAdapter(Context context) throws MQTTBrokerException {

        if(this.MQTTBrokerAdapter == null)
            this.MQTTBrokerAdapter = new MQTTBrokerAdapterImpl(context);

        mqttBrokerTechnology = MQTTBrokerTechnology.getBrokerTechnology(context);

        /*Request permissions*/
        this.MQTTBrokerAdapter.requestPermissions();

        mqttBrokerTechnology.setBrokerAdapter(this.MQTTBrokerAdapter);

        if(!mqttBrokerTechnology.isActive()) {
            mqttBrokerTechnology.start();
            Log.d(TAG, ">> MQTT Broker initialized successfully!");

            try {
                boolean res = MQTTProtocol.getInstance(context).subscribe("#", null);
                if(res)
                    Log.d(TAG, ">> Subscribe in the topic '#': OK");
            } catch (MqttException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initOntologyFramework(Context context) throws OntologyFrameworkException {
        try {
            if (this.ontologyFrameworkAdapter == null)
                this.ontologyFrameworkAdapter = new OntologyFrameworkAdapterImpl(context);
            ontologyFrameworkTechnology = OntologyFrameworkTechnology.getInstance(context);

            /*Request permissions*/
            this.ontologyFrameworkAdapter.requestPermissions();
            ontologyFrameworkTechnology.setOntologyFrameworkAdapter(this.ontologyFrameworkAdapter);

            ontologyFrameworkTechnology.loadKnowledge("");
            ontologyFrameworkTechnology.start();

            Log.d(TAG, ">> Ontology Framework initialized successfully!");
        }catch (Exception e){
            throw new OntologyFrameworkException();
        }
    }

    private void initIoTMiddleware(Context context) throws IoTMiddlewareException {

        if(this.ioTMiddlewareAdapter == null)
            this.ioTMiddlewareAdapter = new IoTMiddlewareAdapterImpl(context);
        ioTMiddlewareTechnology = IoTMiddlewareTechnology.getInstance(context);

        /*Request permissions*/
        this.ioTMiddlewareAdapter.requestPermissions();
        ioTMiddlewareTechnology.setIoTMiddlewareAdapter(this.ioTMiddlewareAdapter);

        IoTMiddlewareListenerImpl ioTMiddlewareListener = new IoTMiddlewareListenerImpl(context);
        ioTMiddlewareTechnology.setListener(ioTMiddlewareListener);

        if(!ioTMiddlewareTechnology.isActive()) {
            ioTMiddlewareTechnology.start();
            Log.d(TAG, ">> IoT Middleware initialized successfully!");
        }
    }

    private void initCEP(Context context) throws CEPException {
        cepEventHandler = CEPEventHandler.getInstance(context);

        /*Adding CEP resource*/
        cepEventHandler.addResourceClass(GPSCEPResource.class);

        if(!cepEventHandler.isActive()) {
            cepEventHandler.start();
            //Log.d(TAG, ">> CEP start *****");
            cepEventHandler.registerEPL();
        }

        Log.d(TAG, ">> CEP initialized successfully!");
    }

    /*This method receives data from IoT middleware and sends it to CEP handler*/
    public void PMSManager(SensorInterface sensor){
        /*Semantic annotation*/
        OntModel semanticObject = (OntModel) this.ontologyFrameworkTechnology.semanticAnnotation(sensor);

        for (CEPResource cepResource : this.cepEventHandler.getCEPResources()) {
            GenericAnnotation genericAnnotation = new GenericAnnotation();
            genericAnnotation.setOntModel(semanticObject);
            if(cepResource.getId().trim().equals(genericAnnotation.returnIdSensor().trim())){
                try {
                    Class[] parameterType = new Class[1];
                    parameterType[0] = OntModel.class;
                    Object obj =  Class.forName("br.ufc.mdcc.cmu.pmslib.cep.resources."+cepResource.getClass().getSimpleName())
                            .getDeclaredConstructor(parameterType).newInstance(semanticObject);
                    this.cepEventHandler.eventHandler(obj);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
//        GPSCEPResource GPSCEPResource = new GPSCEPResource(semanticObject);
//        this.cepEventHandler.eventHandler(GPSCEPResource);

    }

    /*This method receives data from CEP handler and sends to MQTT Broker*/
    public void PMSManager(String topic, Object eventMap){
        mqttProtocol = MQTTProtocol.getInstance(this.context);
        mqttProtocol.publish(topic, null, this.ontologyFrameworkTechnology.getRDF(eventMap));
    }

    public void saveSensorPMS(Object sensor){
        if(!this.activeSensors.contains(sensor))
            this.activeSensors.add(sensor);
    }

    public void removeSensorPMS(Object sensor){
        if(this.activeSensors.contains(sensor))
            this.activeSensors.remove(sensor);
    }

    @Override
    public void addCEPResourceClass(Class cls){
        this.cepEventHandler.addResourceClass(cls);
    }

}
