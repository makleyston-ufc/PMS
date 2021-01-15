package br.ufc.mdcc.cmu.pmslib.iotmiddleware;

import android.util.Log;

import java.util.Random;

import br.ufc.mdcc.cmu.pmslib.exception.IoTMiddlewareException;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.SensorInterface;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.TempSensor;

/**
 * Created by makleyston on 14/01/2021.
 */

public class IoTMiddlewareAdapterImpl extends IoTMiddlewareAdapterInterface {

    private final String TAG = getClass().getSimpleName();

    @Override
    public void onReceiveData(IoTMiddlewareListener ioTMiddlewareListener) {
        receiveDataLocal(ioTMiddlewareListener);
    }

    @Override
    public void start() throws IoTMiddlewareException {
        Log.d(TAG, "startIoTMiddleware");
    }

    @Override
    public void stop() throws IoTMiddlewareException {
        Log.d(TAG, "stopIoTMiddleware");
    }

    /*Test implementation*/
    private void receiveDataLocal(final IoTMiddlewareListener ioTMiddlewareListener){
        //Log.d(TAG, "receiveDataLocal");
        final Random random = new Random();
        final TempSensor tempSensor = new TempSensor();
        new Thread(new Runnable() {
            public void run() {
                try {
                    while(true) {
                        tempSensor.setValue(random.nextInt() + "");
                        //Log.d(TAG, "receiveDataLocal loop "+tempSensor.getValue());
                        ioTMiddlewareListener.onReceiveData(tempSensor);
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
