package br.ufc.mdcc.cmu.pmslib;

import br.ufc.mdcc.cmu.pmslib.cep.StatementSubscriber;
import br.ufc.mdcc.cmu.pmslib.cep.CEPResource;
import br.ufc.mdcc.cmu.pmslib.exception.PMSException;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.IoTMiddlewareAdapterInterface;
import br.ufc.mdcc.cmu.pmslib.mqttbroker.MQTTBrokerAdapterInterface;

public interface PMSInterface {

    /**
     * This method starts the PMS
     * @throws PMSException
     */
    public void start() throws PMSException;

    /**
     * This method stops the PMS
     * @throws PMSException
     */
    public void stop() throws PMSException;

    /**
     * Register the EPL statement classes in the CEP engine.
     * Implement a StatementSubscriber extension
     * @see StatementSubscriber
     * @param resourceClass
     */
    public void addCEPRule(StatementSubscriber resourceClass);


    public void addCEPResourceClass(Class<? extends CEPResource> cls);

    //public void setOntologyFrameworkAdapter(OntologyFrameworkAdapterInterface ontologyFramework);

    /**
     * Set an IoT middleware to scanner the environment.
     * The IoT middleware default is Google Awareness.
     * There is also implemented the Loccam middleware. To turner the middleware to Loccam,
     * then create an Loccam instance and past for this method.
     * @see com.google.android.gms.awareness.Awareness
     * @see br.ufc.mdcc.cmu.pmslib.iotmiddleware.loccam.ContextManager
     * @param ioTMiddlewareAdapter
     */
    public void setIoTMiddlewareAdapter(IoTMiddlewareAdapterInterface ioTMiddlewareAdapter);

    /**
     * Set a MQTT broker.
     * The MQTT broker default is the Moquette.
     * @see org.eclipse.moquette.server.Server
     * @param mqttBrokerAdapter
     */
    public void setMQTTBrokerAdapter(MQTTBrokerAdapterInterface mqttBrokerAdapter);

}
