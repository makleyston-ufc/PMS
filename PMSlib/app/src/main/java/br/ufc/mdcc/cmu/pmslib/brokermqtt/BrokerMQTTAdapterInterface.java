package br.ufc.mdcc.cmu.pmslib.brokermqtt;

public interface BrokerMQTTAdapterInterface {

    /**
     * This method is called to start the MQTT broker
     */
    public void start();

    /**
     * This method is called to stop the MQTT broker
     */
    public void stop();

    /**
     * This method is called to verify if that the broker is active
     * @return boolean [true for active and false for inactive]
     */
    public boolean isActive();

}
