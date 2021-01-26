package br.ufc.mdcc.cmu.pmslib.cep;

import android.content.Context;
import android.util.Log;

import androidx.annotation.CallSuper;

import java.util.ArrayList;
import java.util.List;

import br.ufc.mdcc.cmu.pmslib.PMS;
import br.ufc.mdcc.cmu.pmslib.mqttbroker.MQTTProtocol;
import br.ufc.mdcc.cmu.pmslib.ontology.OntologyFrameworkTechnology;

/**
 * Created by makleyston on 14/01/2021
 */

public abstract class StatementSubscriber {
    private Context context;
    private PMS pms = null;
    private List<String> domains = new ArrayList<String>();

    public StatementSubscriber(Context context){
        this.context = context;
        this.pms = PMS.getInstance(context);
    }

    public void setContext(Context context){this.context = context;}

    public Context getContext(){return this.context;}

    /**
     * This method will be called by the CEP to
     * find the pattern in the data stream.
     * @return String. A query statement to be inserted on CEP.
     */
    public abstract String getStatement();

    private final String TAG = getClass().getSimpleName();

    /**
     * This method will be
     * @param topic
     */
    public void addDomain(String topic){
        this.domains.add(topic);
    }

    /**
     * The method is called always a event is finded in the data stream.
     * @param eventMap An object instance eventMap.
     */
    @CallSuper
    public void update(Object eventMap){
        if(eventMap != null){
            for (String topic: domains) {
                Log.d(TAG, ">> evento encontrado!! "+eventMap.getClass().getSimpleName());
                pms.PMSManager(topic, eventMap);
            }
        }
    }
}
