package br.ufc.mdcc.cmu.pmslib;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.VCARD;

import java.util.ArrayList;
import java.util.List;

import br.ufc.mdcc.cmu.pmslib.cep.CEPEventHandler;
import br.ufc.mdcc.cmu.pmslib.cep.StatementSubscriber;
import br.ufc.mdcc.cmu.pmslib.cep.statements.GPSStatement;
import br.ufc.mdcc.cmu.pmslib.cep.statements.ResourceStatement;
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
    public void addCEPRuleClass(Class<StatementSubscriber> resourceClass) {
        this.cepEventHandler.addCEPRuleClass(((Class<StatementSubscriber>) resourceClass));
    }

//    @Override
//    public void setOntologyFrameworkAdapter(OntologyFrameworkAdapterInterface ontologyFramework) {
//        this.ontologyFrameworkAdapter = ontologyFramework;
//    }

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

        if(!mqttBrokerTechnology.isActive())
            mqttBrokerTechnology.start();

        Log.d(TAG, ">> MQTT Broker initialized successfully!");
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
        //IoTMiddlewareAdapterInterface ioTMiddlewareAdapter = new IoTMiddlewareAdapterImpl(context);
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

        /*Adding resource sensor*/
        cepEventHandler.addResourceClass(Resource.class);

        /*Adding CEP rule*/
        cepEventHandler.addCEPRuleClass(GPSStatement.class);
        cepEventHandler.addCEPRuleClass(ResourceStatement.class);

        if(!cepEventHandler.isActive())
            cepEventHandler.start();

        Log.d(TAG, ">> CEP initialized successfully!");

    }

    /*This method receives data from IoT middleware and sends it to CEP handler*/
    public void PMSManager(SensorInterface sensor){
        //Log.d(TAG, ">> Dados recebidos do IoT Middleware");
        /*Semantic annotation*/
        Object obj = this.ontologyFrameworkTechnology.semanticAnnotation(sensor);
        Gson gson = new Gson();
        Log.d(TAG, "*** >> "+gson.toJson(((Resource)obj).getProperty(VCARD.N).getString()));
        /*CEP analizyses*/
        //Log.d(TAG, ">> Dados recebidos do framework de Ontologias"+((Resource) obj).getProperty(VCARD.FN));
        this.cepEventHandler.eventHandler(obj);
    }

    /*This method receives data from CEP handler and sends to MQTT Broker*/
    public void PMSManager(String topic, Object eventMap){
        mqttProtocol = MQTTProtocol.getInstance(this.context);
        mqttProtocol.publish(topic+"/"+this.ontologyFrameworkTechnology.getRDF(eventMap));
    }

    public void saveSensorPMS(Object sensor){
        if(!this.activeSensors.contains(sensor))
            this.activeSensors.add(sensor);
    }

    public void removeSensorPMS(Object sensor){
        if(this.activeSensors.contains(sensor))
            this.activeSensors.remove(sensor);
    }
}
