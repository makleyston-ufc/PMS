package br.ufc.mdcc.cmu.pmslib.exception;

public class MQTTBrokerException extends Exception {
    public MQTTBrokerException() {
        super("MQTT Broker shows malfunction!");
    }
}
