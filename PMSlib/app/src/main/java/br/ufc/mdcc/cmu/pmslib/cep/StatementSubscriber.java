package br.ufma.lsdi.iottv.dataprocessing.cep.subscribe;

import android.content.Context;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import br.ufma.lsdi.iottv.dataprocessing.model.PortableDevice;
import br.ufma.lsdi.iottv.dataprocessing.mqtt.MQTTImpl;

/**
 * Created by makleyston on 29/01/18.
 * Updated by makleyston on 27/04/20.
 * @author makleyston
 * @version 1.0.1
 */

public abstract class StatementSubscriber {
    private Context context;
    private String label;
    private MQTTImpl mqtt = null;

    public StatementSubscriber(){
        this(null, null);
    }

    public StatementSubscriber(Context context, String label) {
        this.context = context;
        this.label = label;
        this.mqtt = MQTTImpl.getInstance(context);
    }

    public void setContext(Context context){this.context = context;}

    public Context getContext(){return this.context;}

    /**
     * This method will be called by the CEP to
     * find the pattern in the data flow of the device itself.
     * @return String A query statement to be inserted on CEP
     */
    public abstract String getStatement();

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * The method is called always a event is finded in the data flow
     * of device itself.
     * @param eventMap An object instance of type specified on Multimodal Interaction
     */
    @CallSuper
    public void update(Object eventMap){
        if(eventMap != null){
            //Publishing multimodal interaction label on mqtt broker
            mqtt.publishMultimodalInteraction(getLabel());
        }
    }
}
