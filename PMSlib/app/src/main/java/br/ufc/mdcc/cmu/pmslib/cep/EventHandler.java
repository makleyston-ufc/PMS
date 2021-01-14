package br.ufma.lsdi.iottv.dataprocessing.cep.handler;

import android.content.Context;
import android.util.Log;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

import java.util.ArrayList;

import br.ufma.lsdi.iottv.dataprocessing.cep.subscribe.StatementSubscriber;
import br.ufma.lsdi.iottv.dataprocessing.model.multimodalinteractions.event.Event;
import br.ufma.lsdi.iottv.dataprocessing.model.multimodalinteractions.event.EventType;
import br.ufma.lsdi.iottv.dataprocessing.model.multimodalinteractions.modality.Modality;
import br.ufma.lsdi.iottv.dataprocessing.model.sensor.Sensor;

/**
 * Created by makleyston on 29/01/18.
 */

public final class EventHandler{

    public static EventHandler instance = null;
    private Context context = null;
    private ArrayList<Class<Sensor>> sensorClassArrayList = new ArrayList<>();
    private boolean isInitiated = false;

    public static EventHandler getInstance(Context context){
        if(instance == null){
            instance = new EventHandler();
        }
        instance.setContext(context);
        return instance;
    }

    public boolean isInitiated() {
        return isInitiated;
    }

    private void setInitiated(boolean initiated) {
        isInitiated = initiated;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private EventHandler(){}

    /** Esper service */
    private EPServiceProvider epService;
    private EPStatement epStatement;
    final private String TAG = getClass().getName().toString();

    public void handle(Sensor evt){
        for (Class<Sensor> s: sensorClassArrayList) {
            if(evt.getClass() == s) {
                epService.getEPRuntime().sendEvent(evt);
            }
        }
    }

    public void initService() {
        if(this.isInitiated) return;
        Configuration cepConfig = new Configuration();
        for (Class<Sensor> s: sensorClassArrayList) {
            cepConfig.addEventType(s.getSimpleName(), s.getName());
        }
        epService = EPServiceProviderManager.getDefaultProvider(cepConfig);
        setInitiated(true);
        Log.i(TAG, "CEP started successfully!");
    }

    public void endService(){
        epStatement.stop();
        epService.destroy();
        setInitiated(false);
    }

    public void stopService(){
        //epStatement.stop();
    }

    public void resumeService(){
        epStatement.start();
    }

    public void removeAllStatement(){
        epService.getEPAdministrator().destroyAllStatements();
    }

    //Revisar
    public void addCEPEvent(Modality[] modalities){
        if(modalities.length == 0){
            Log.d(TAG, "No StatementSubscriber class was specified!");
        }else{
            //Populating the sensor list
            for (Modality modality: modalities) {
                for (Event e: modality.getEvent()) {
                    this.addSensorClass(e.getEventType().getSensorClass());
                }
            }
            //Starting the CEP
            this.initService();

            //Registring the sensors statements
            for (Modality modality: modalities) {
                for (Event e: modality.getEvent()) {
                    //if(e.getEventType() instanceof Pointing) {
                        //Pointing pointing = (Pointing) e.getEventType();
                        EventType eventType = e.getEventType();
                        StatementSubscriber st = null;
                        try {
                            st = eventType.getStatementSubscriberClass().newInstance();
                            st.setContext(context);
                            st.setLabel(e.getLabel());
                            epStatement = epService.getEPAdministrator().createEPL(st.getStatement());
                            epStatement.setSubscriber(st);
                        } catch (IllegalAccessException ex) {
                            ex.printStackTrace();
                        } catch (InstantiationException ex) {
                            ex.printStackTrace();
                        }
                    //}else if(e.getEventType() instanceof Talking){
                        //TOD fazer o tratamento se for comando de voz
                    //}
                    //TOD fazer o tratamento para outros comandos
                Log.d(TAG, "The StatementSubscriber of '"+modality.getClass().getName()
                        +"' '"+modality.getLabel()+"' have been successfully inserted!");
            }
            Log.d(TAG, "The StatementSubscriber classes have been successfully inserted!");
        }


        }
    }

    private void addSensorClass(Class<Sensor> sensorClass){
        this.sensorClassArrayList.add(sensorClass);
    }
}
