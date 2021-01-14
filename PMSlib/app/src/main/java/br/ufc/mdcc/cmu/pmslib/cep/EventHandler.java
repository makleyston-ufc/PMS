package br.ufc.mdcc.cmu.pmslib.cep;

import android.content.Context;
import android.hardware.Sensor;
import android.util.Log;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

import java.util.ArrayList;

/**
 * Created by makleyston on 14/01/2021
 */

public final class EventHandler{

    public static EventHandler instance = null;
    private Context context = null;
    private ArrayList<Class<Sensor>> sensorClassArrayList = new ArrayList<>();
    private boolean active = false;

    private EventHandler(){}

    public static EventHandler getInstance(Context context){
        if(instance == null){
            instance = new EventHandler();
            instance.setContext(context);
        }
        return instance;
    }

    public boolean isActived() {
        return this.active;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /** Asper service **/
    private EPServiceProvider epService;
    private EPStatement epStatement;
    final private String TAG = getClass().getSimpleName();

    public void handle(Sensor evt){
        for (Class<Sensor> s: sensorClassArrayList) {
            if(evt.getClass() == s) {
                epService.getEPRuntime().sendEvent(evt);
            }
        }
    }

    public void initService() {
        if(this.active) return;
        Configuration cepConfig = new Configuration();
        for (Class<Sensor> s: sensorClassArrayList) {
            cepConfig.addEventType(s.getSimpleName(), s.getName());
        }
        epService = EPServiceProviderManager.getDefaultProvider(cepConfig);
        this.active = true;
        Log.i(TAG, "CEP started successfully!");
    }

    public void endService(){
        epStatement.stop();
        epService.destroy();
        this.active = false;
    }

    public void stopService(){
        //epStatement.stop();
        this.active = false;
    }

    public void resumeService(){
        epStatement.start();
        this.active = true;
    }

    public void removeAllStatement(){
        epService.getEPAdministrator().destroyAllStatements();
    }

    private void addSensorClass(Class<Sensor> sensorClass){
        this.sensorClassArrayList.add(sensorClass);
    }
}
