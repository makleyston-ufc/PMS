package br.ufc.mdcc.cmu.pmslib.mqttbroker;

import br.ufc.mdcc.cmu.pmslib.exception.BrokerMQTTException;

/**
 * Created by makleyston on 14/01/2021
 */

public interface MQTTBrokerAdapterInterface {

    /**
     * This method is called to start the MQTT broker
     */
    public void start() throws BrokerMQTTException;

    /**
     * This method is called to stop the MQTT broker
     */
    public void stop() throws BrokerMQTTException;

    /**
     * This method is called to verify if that the broker is active
     * @return boolean [true for active and false for inactive]
     */
    public boolean isActive();

}
