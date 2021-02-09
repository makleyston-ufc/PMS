package br.ufc.mdcc.cmu.pmslib.cep;

import android.content.Context;
import android.util.Log;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import java.util.ArrayList;
import br.ufc.mdcc.cmu.pmslib.cep.resources.GPSCEPResource;
import br.ufc.mdcc.cmu.pmslib.exception.CEPException;

/**
 * Created by makleyston on 14/01/2021
 */

public final class CEPEventHandler {

    public static CEPEventHandler instance = null;
    private Context context = null;
    private ArrayList<Class<? extends CEPResource>> resourceClasses = new ArrayList<>();
    private ArrayList<CEPResource> cepResources = new ArrayList<>();

    private boolean active = false;

    /** Asper service **/
    private EPServiceProvider epService;
//    private EPStatement epStatement;
    final private String TAG = getClass().getSimpleName();


    private CEPEventHandler(){}

    public static CEPEventHandler getInstance(Context context){
        if(instance == null){
            instance = new CEPEventHandler();
            instance.setContext(context);
        }
        return instance;
    }

    public void eventHandler(Object obj){
        for (Class s: this.resourceClasses) {
            if(obj.getClass() == s) {
                epService.getEPRuntime().sendEvent(obj);
            }
        }
    }

    public void start() throws CEPException {
        if(this.active) return;
        Configuration cepConfig = new Configuration();
        for (Class s: this.resourceClasses) {
            cepConfig.addEventType(s.getSimpleName(), s.getName());
        }
        epService = EPServiceProviderManager.getDefaultProvider(cepConfig);
        this.active = true;
        Log.i(TAG, "CEP started successfully!");
    }

    public void stop() throws CEPException{
        try{
//        epStatement.stop();
        epService.destroy();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            Log.i(TAG, "CEP stopped successfully!");
            this.active = false;
        }
    }

    public void resumeService(){
//        epStatement.start();
        this.active = true;
    }

    public void removeAllStatement(){
        epService.getEPAdministrator().destroyAllStatements();
    }

    public void addResourceClass(Class<? extends CEPResource> resourceClass){
        this.resourceClasses.add(resourceClass);
    }

    public ArrayList<Class<? extends CEPResource>> getCEPResourceClasses(){
        return this.resourceClasses;
    }

    public ArrayList<CEPResource> getCEPResources(){
        return this.cepResources;
    }

    public void registerEPL() {
        for (Class stmSb : resourceClasses) {
            try {
                CEPResource cepResource = ((CEPResource)stmSb.newInstance());
                EPStatement epStatement = epService.getEPAdministrator().createEPL(cepResource.getStatement());
                this.cepResources.add(cepResource);
                epStatement.setSubscriber(cepResource);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        epService.getEPAdministrator().startAllStatements();
    }

    public boolean isActive() {
        return this.active;
    }

    private void setContext(Context context) {
        this.context = context;
    }
}
