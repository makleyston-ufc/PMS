package br.ufc.mdcc.cmu.pmslib.iotmiddleware;

import android.content.Context;
import android.util.Log;

import androidx.annotation.CallSuper;

/**
 * Created by makleyston on 14/01/2021.
 */

import br.ufc.mdcc.cmu.pmslib.exception.IoTMiddlewareException;

/**
 * Created by makleyston on 14/01/2021
 * @author makleyston
 * @version 1.0.0
 */

public abstract class IoTMiddlewareAdapterInterface{

    private IoTMiddlewareListener ioTMiddlewareListener = null;

    private final String TAG = getClass().getSimpleName();

    private Context context;

    public IoTMiddlewareListener getIoTMiddlewareListener() throws IoTMiddlewareException {
        if(ioTMiddlewareListener == null) {
            Log.i(TAG, "IoT middleware listener is misses!");
            throw new IoTMiddlewareException();
        }else
            return ioTMiddlewareListener;
    }

    public void setIoTMiddlewareListener(IoTMiddlewareListener ioTMiddlewareListener) {
        this.ioTMiddlewareListener = ioTMiddlewareListener;
    }

    public abstract void onReceiveData(IoTMiddlewareListener ioTMiddlewareListener);

    /**
     * This method start the IoT middleware
     * @throws IoTMiddlewareException
     */
    public abstract void start() throws IoTMiddlewareException;

    /**
     * This method stop the IoT middleware
     * @throws IoTMiddlewareException
     */
    public abstract void stop() throws IoTMiddlewareException;

    /**
     * Set a listener
     * @param ioTMiddlewareListener An instance of type IoTMiddlewareListener
     */
    public void setListener(IoTMiddlewareListener ioTMiddlewareListener){
        this.ioTMiddlewareListener = ioTMiddlewareListener;
        this.onReceiveData(ioTMiddlewareListener);
    }

    /**
     * Get the listener
     * @return ioTMiddlewareListener IoTMiddlewareListener
     */
    public IoTMiddlewareListener getListener(){
        return this.ioTMiddlewareListener;
    }

    /**
     * Get the context
     * @return context Context
     */
    public Context getContext() {
        return context;
    }

    /**
     * Set a context
     * @param context Context
     */
    public void setContext(Context context) {
        this.context = context;
    }

}
