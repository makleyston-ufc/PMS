package br.ufc.mdcc.cmu.pmslib.iotmiddleware;

import android.content.Context;

import androidx.annotation.CallSuper;

/**
 * Created by makleyston on 14/01/2021.
 */

public abstract class IoTMiddlewareListener {

    private Context context = null;

    public IoTMiddlewareListener(Context context){
        this.context = context;
    }

    /**
     * This method is called whenever that the IoT middleware
     * reads data from sensors
     * @param sensor An instance of type SensorInterface
     */
    @CallSuper
    public void onReceiveData(SensorInterface sensor){

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
