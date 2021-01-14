package br.ufc.mdcc.cmu.pmslib.cep;

import android.content.Context;

import androidx.annotation.CallSuper;

import br.ufc.mdcc.cmu.pmslib.mqttbroker.MQTTProtocol;

/**
 * Created by makleyston on 14/01/2021
 */

public abstract class StatementSubscriber {
    private Context context;
    private String description;
    private MQTTProtocol mqttProtocol = null;

    public StatementSubscriber(){}

    public void setContext(Context context){this.context = context;}

    public Context getContext(){return this.context;}

    /**
     * This method will be called by the CEP to
     * find the pattern in the data stream.
     * @return String. A query statement to be inserted on CEP.
     */
    public abstract String getStatement();

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return this.description;
    }

    /**
     * The method is called always a event is finded in the data stream.
     * @param eventMap An object instance eventMap.
     */
    @CallSuper
    public void update(Object eventMap){
        if(eventMap != null){
            mqttProtocol = MQTTProtocol.getInstance(this.context);
            mqttProtocol.publish(getDescription());
        }
    }
}
