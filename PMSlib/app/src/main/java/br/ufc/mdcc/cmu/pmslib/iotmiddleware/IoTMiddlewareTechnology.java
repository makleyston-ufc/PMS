package br.ufc.mdcc.cmu.pmslib.iotmiddleware;

import android.content.Context;

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

    @Override
    public boolean isActive() {
        return this.ioTMiddlewareAdapter.isActive();
    }

    public void setIoTMiddlewareAdapter(IoTMiddlewareAdapterInterface ioTMiddlewareAdapter){
        this.ioTMiddlewareAdapter = ioTMiddlewareAdapter;
    }

    @Override
    public void onReceiveData(IoTMiddlewareListener ioTMiddlewareListener) {
        this.ioTMiddlewareAdapter.onReceiveData(ioTMiddlewareListener);
    }

    @Override
    public void start() throws IoTMiddlewareException {
        this.ioTMiddlewareAdapter.start();
    }

    @Override
    public void stop() throws IoTMiddlewareException {
        this.ioTMiddlewareAdapter.stop();
    }

    public IoTMiddlewareAdapterInterface getIoTMiddlewareAdapter(){
        return this.ioTMiddlewareAdapter;
    }
}
