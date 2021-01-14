package br.ufc.mdcc.cmu.pmslib.iotmiddleware;

import android.content.Context;
import android.util.Log;

/**
 * Created by makleyston on 14/01/2021.
 */

public final class IoTMiddlewareListenerImpl extends IoTMiddlewareListener {
    private final String TAG = getClass().getSimpleName();

    public IoTMiddlewareListenerImpl(Context context) {
        super(context);
    }

    @Override
    public void onReceiveData(SensorInterface sensor) {
        super.onReceiveData(sensor);
        Log.i(TAG, ">> onReceiveData");
    }

    @Override
    public void onSensorDisconnected(String id) {
        super.onSensorDisconnected(id);
        Log.i(TAG, ">> onSensorDisconnected");
    }

    @Override
    public void onSensorDiscovered(SensorInterface sensor) {
        super.onSensorDiscovered(sensor);
        Log.i(TAG, ">> onSensorDiscovered");
    }
}
