package br.ufc.mdcc.cmu.pmslib.iotmiddleware;

import android.content.Context;

import androidx.annotation.CallSuper;

import br.ufc.mdcc.cmu.pmslib.PMS;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.SensorInterface;
import br.ufc.mdcc.cmu.pmslib.ontology.OntologyFrameworkTechnology;

/**
 * Created by makleyston on 14/01/2021
 */

public abstract class IoTMiddlewareListener {

    private Context context = null;
    private PMS pms = null;

    public IoTMiddlewareListener(Context context){
        this.context = context;
        this.pms = PMS.getInstance(context);
    }

    /**
     * This method is called whenever that the IoT middleware
     * reads data from sensors
     * @param sensor An instance of type SensorInterface
     */
    @CallSuper
    public void onReceiveData(SensorInterface sensor){
        pms.PMSManager(sensor);

    }

    /**
     * This method is called whenever that a sensor to disconnect
     * @param id String
     */
    @CallSuper
    public void onSensorDisconnected(String id){

    }

    /**
     * This method is called whenever that a sensor is
     * discovery by IoT middleware
     * @param sensor An instance of type SensorInterface
     */
    @CallSuper
    public void onSensorDiscovered(SensorInterface sensor){

    }

}
