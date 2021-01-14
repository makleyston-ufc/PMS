package br.ufc.mdcc.cmu.pmslib.iotmiddleware;

import android.util.Log;

import br.ufc.mdcc.cmu.pmslib.exception.IoTMiddlewareException;

/**
 * Created by makleyston on 14/01/2021.
 */

public class IoTMiddlewareAdapterImpl extends IoTMiddlewareAdapterInterface {

    private final String TAG = getClass().getSimpleName();

    @Override
    public void receiveData(IoTMiddlewareListener ioTMiddlewareListener) throws IoTMiddlewareException {
        Log.d(TAG, "receiveData");
    }

    @Override
    public void startIotMiddleware() throws IoTMiddlewareException {
        Log.d(TAG, "startIoTMiddlware");
    }

    @Override
    public void stopIotMiddleware() throws IoTMiddlewareException {
        Log.d(TAG, "stopIoTMiddleware");
    }
}
