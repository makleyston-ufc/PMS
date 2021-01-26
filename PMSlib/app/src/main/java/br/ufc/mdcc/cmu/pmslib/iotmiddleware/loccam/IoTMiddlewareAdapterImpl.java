package br.ufc.mdcc.cmu.pmslib.iotmiddleware.loccam;

import android.content.Context;
import android.util.Log;

import java.util.Random;

import br.ufc.mdcc.cmu.pmslib.exception.IoTMiddlewareException;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.IoTMiddlewareAdapterInterface;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.loccam.ContextKeys;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.loccam.ContextListener;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.loccam.ContextManager;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.SensorInterface;

/**
 * Created by makleyston on 14/01/2021.
 */

public class IoTMiddlewareAdapterImpl extends IoTMiddlewareAdapterInterface {

    private final String TAG = getClass().getSimpleName();
    private ContextListener contextListener = null;

    public IoTMiddlewareAdapterImpl(Context context){
        super(context);

        ContextManager.getInstance().connect(context,"LoccamConnection");

        contextListener = new ContextListener() {
            @Override
            public void onContextReady(String data) {
                Log.d(TAG, "Readed value from Loccam is: "+data);
                SensorInterface sensor = new SensorInterface() {};
                //sensor.setValue(data);
                getListener().onReceiveData(sensor);
            }

            @Override
            public String getContextKey() {
                return ContextKeys.CKs.get(0);//PROXIMITY
            }
        };

        ContextManager.getInstance().registerListener(contextListener);
    }
//
//    @Override
//    public void onReceiveData(IoTMiddlewareListener ioTMiddlewareListener) {
//
//    }

    @Override
    public void start() throws IoTMiddlewareException {
        Log.d(TAG, "startIoTMiddleware");
        ContextManager.getInstance().initLoccam(ContextKeys.CKs);
    }

    @Override
    public void stop() throws IoTMiddlewareException {
        Log.d(TAG, "stopIoTMiddleware");
        //TODO
    }

    @Override
    public boolean isActive() {
        return true;
        //TODO
    }

    @Override
    public void requestPermissions() {
        //TODO
    }

}
