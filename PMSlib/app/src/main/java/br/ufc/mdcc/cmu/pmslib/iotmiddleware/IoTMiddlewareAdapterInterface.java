package br.ufc.mdcc.cmu.pmslib.iotmiddleware;

import android.content.Context;

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

public abstract class IoTMiddlewareAdapterInterface {

    private IoTMiddlewareListener ioTMiddlewareListener = null;

    private Context context;

    /**
     * This method is called whenever the IoT middleware to read
     * data from sensors
     * @param ioTMiddlewareListener
     * @throws IoTMiddlewareException
     */
    public abstract void receiveData(IoTMiddlewareListener ioTMiddlewareListener) throws IoTMiddlewareException;

    /**
     * This method start the IoT middleware
     * @throws IoTMiddlewareException
     */
    public abstract void startIotMiddleware() throws IoTMiddlewareException;

    /**
     * This method stop the IoT middleware
     * @throws IoTMiddlewareException
     */
    public abstract void stopIotMiddleware() throws IoTMiddlewareException;

    /**
     * Set a listener
     * @param ioTMiddlewareListener An instance of type IoTMiddlewareListener
     */
    public void setListener(IoTMiddlewareListener ioTMiddlewareListener){
        this.ioTMiddlewareListener = ioTMiddlewareListener;
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
