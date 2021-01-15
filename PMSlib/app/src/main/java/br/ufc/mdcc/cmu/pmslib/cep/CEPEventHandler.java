package br.ufc.mdcc.cmu.pmslib.cep;

import android.content.Context;
import android.util.Log;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

import java.util.ArrayList;

import br.ufc.mdcc.cmu.pmslib.exception.CEPException;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.SensorInterface;

/**
 * Created by makleyston on 14/01/2021
 */

public final class CEPEventHandler {

    public static CEPEventHandler instance = null;
    private Context context = null;
    private ArrayList<Class<SensorInterface>> sensorClassArrayList = new ArrayList<>();
    private boolean active = false;
    /** Asper service **/
    private EPServiceProvider epService;
    private EPStatement epStatement;
    final private String TAG = getClass().getSimpleName();


    private CEPEventHandler(){}

    public static CEPEventHandler getInstance(Context context){
        if(instance == null){
            instance = new CEPEventHandler();
            instance.setContext(context);
        }
        return instance;
    }

    public void eventHandle(SensorInterface evt){
        for (Class<SensorInterface> s: sensorClassArrayList) {
            if(evt.getClass() == s) {
                epService.getEPRuntime().sendEvent(evt);
            }
        }
    }

    public void start() throws CEPException {
        if(this.active) return;
        Configuration cepConfig = new Configuration();
        for (Class<SensorInterface> s: sensorClassArrayList) {
            cepConfig.addEventType(s.getSimpleName(), s.getName());
        }
        epService = EPServiceProviderManager.getDefaultProvider(cepConfig);
        epService.initialize(); //Verificar se está correto!
        this.active = true;
        Log.i(TAG, "CEP started successfully!");
    }

    public void stop() throws CEPException{
        try{
        epStatement.stop();
        epService.destroy();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            Log.i(TAG, "CEP stopped successfully!");
            this.active = false;
        }
    }

    public void resumeService(){
        epStatement.start();
        this.active = true;
    }

    public void removeAllStatement(){
        epService.getEPAdministrator().destroyAllStatements();
    }

    public void addSensorClass(Class<SensorInterface> sensorClass){
        this.sensorClassArrayList.add(sensorClass);
    }


    public boolean isActive() {
        return this.active;
    }

    private void setContext(Context context) {
        this.context = context;
    }
}
