package br.ufc.mdcc.cmu.pmslib.iotmiddleware;

import android.content.Context;
import android.util.Log;

import br.ufc.mdcc.cmu.pmslib.exception.IoTMiddlewareException;

/**
 * Created by makleyston on 14/01/2021.
 */

public final class IoTMiddlewareTechnology extends IoTMiddlewareAdapterInterface {

    private static IoTMiddlewareTechnology instance = null;

    private final String TAG = getClass().getSimpleName();

    private IoTMiddlewareAdapterInterface ioTMiddlewareAdapter = null;

    private IoTMiddlewareTechnology(){}

    public static IoTMiddlewareTechnology getInstance(Context context){
        if(instance == null){
            instance = new IoTMiddlewareTechnology();
            instance.setContext(context);
        }
        return instance;
    }

    @Override
    public void setListener(IoTMiddlewareListener ioTMiddlewareListener) {
        this.ioTMiddlewareAdapter.setListener(ioTMiddlewareListener);
    }

    public void setIoTMiddlewareAdapter(IoTMiddlewareAdapterInterface ioTMiddlewareAdapter){
        this.ioTMiddlewareAdapter = ioTMiddlewareAdapter;
    }

    @Override
    public void receiveData(IoTMiddlewareListener ioTMiddlewareListener) throws IoTMiddlewareException {
        this.ioTMiddlewareAdapter.receiveData(ioTMiddlewareListener);
    }

    @Override
    public void startIotMiddleware() throws IoTMiddlewareException {
        this.ioTMiddlewareAdapter.startIotMiddleware();
    }

    @Override
    public void stopIotMiddleware() throws IoTMiddlewareException {
        this.ioTMiddlewareAdapter.stopIotMiddleware();
    }

    public IoTMiddlewareAdapterInterface getIoTMiddlewareAdapter(){
        return this.ioTMiddlewareAdapter;
    }
}
