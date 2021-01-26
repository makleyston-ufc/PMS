package br.ufc.mdcc.cmu.pmslib.cep;

import android.content.Context;
import android.util.Log;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

import java.util.ArrayList;

import br.ufc.mdcc.cmu.pmslib.cep.resources.Resource;
import br.ufc.mdcc.cmu.pmslib.exception.CEPException;

/**
 * Created by makleyston on 14/01/2021
 */

public final class CEPEventHandler {

    public static CEPEventHandler instance = null;
    private Context context = null;
    private ArrayList<Class<? extends Resource>> resourceClasses = new ArrayList<>();
    private ArrayList<StatementSubscriber> CEPRuleClasses = new ArrayList<>();
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

    public void eventHandler(Object obj){
        //Log.d(TAG, ">> event handler");
        for (Class<? extends Resource> s: this.resourceClasses) {
            if(obj.getClass() == s) {
                //Log.d(TAG, ">> event handler: encontrado evento "+obj.getClass().getSimpleName());
                epService.getEPRuntime().sendEvent(obj);
            }
        }
    }

    public void start() throws CEPException {
        if(this.active) return;
        Configuration cepConfig = new Configuration();
        for (Class<? extends Resource> s: this.resourceClasses) {
            cepConfig.addEventType(s.getSimpleName(), s.getName());
        }
        epService = EPServiceProviderManager.getDefaultProvider(cepConfig);
        //epService.initialize(); //Verificar se está correto!
        this.active = true;
        Log.i(TAG, "CEP started successfully!");
    }


    public void stop() throws CEPException{
        try{
//        epStatement.stop();
//        epService.destroy();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            Log.i(TAG, "CEP stopped successfully!");
            this.active = false;
        }
    }

    public void resumeService(){
        //epStatement.start();
        this.active = true;
    }

    public void removeAllStatement(){
        epService.getEPAdministrator().destroyAllStatements();
    }

    public void addCEPRule(StatementSubscriber stm){
        this.CEPRuleClasses.add(stm);
    }

    public void addResourceClass(Class<? extends Resource> resourceClass){
        this.resourceClasses.add(resourceClass);
    }

    public void registerEPL() {
        for (StatementSubscriber stmSb : CEPRuleClasses) {
            try {
                //Log.d(TAG, ">> getStatement: "+stmSb.getStatement());
                epStatement = epService.getEPAdministrator().createEPL(stmSb.getStatement());
                epStatement.setSubscriber(stmSb);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isActive() {
        return this.active;
    }

    private void setContext(Context context) {
        this.context = context;
    }
}
