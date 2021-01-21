package br.ufc.mdcc.cmu.pmslib.iotmiddleware;

import android.content.Context;

import br.ufc.mdcc.cmu.pmslib.exception.IoTMiddlewareException;

/**
 * Created by makleyston on 14/01/2021.
 */

public final class IoTMiddlewareTechnology extends IoTMiddlewareAdapterInterface {

    private static IoTMiddlewareTechnology instance = null;

    private IoTMiddlewareAdapterInterface ioTMiddlewareAdapter = null;

    private IoTMiddlewareTechnology(Context context){
        super(context);
    }

    public static IoTMiddlewareTechnology getInstance(Context context){
        if(instance == null){
            instance = new IoTMiddlewareTechnology(context);
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
