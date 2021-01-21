package br.ufc.mdcc.cmu.pmslib.mqttbroker;

import androidx.annotation.CallSuper;

import br.ufc.mdcc.cmu.pmslib.exception.MQTTBrokerException;

/**
 * Created by makleyston on 14/01/2021
 */

public abstract class MQTTBrokerAdapterInterface {

    /**
     * This method is called to start the MQTT broker
     */
    public abstract void start() throws MQTTBrokerException;

    /**
     * This method is called to stop the MQTT broker
     */
    public abstract void stop() throws MQTTBrokerException;

    /**
     * This method is called to verify if that the broker is active
     * @return boolean [true for active and false for inactive]
     */
    public abstract boolean isActive();

}
