package br.ufc.mdcc.cmu.pmslib;

import android.content.Context;

import br.ufc.mdcc.cmu.pmslib.cep.StatementSubscriber;
import br.ufc.mdcc.cmu.pmslib.exception.PMSException;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.IoTMiddlewareAdapterInterface;
import br.ufc.mdcc.cmu.pmslib.mqttbroker.MQTTBrokerAdapterInterface;
import br.ufc.mdcc.cmu.pmslib.ontology.OntologyFrameworkAdapterInterface;

public interface PMSInterface {

    public void start() throws PMSException;
    public void stop() throws PMSException;
    public void addCEPRuleClass(Class<StatementSubscriber> resourceClass);
    //public void setOntologyFrameworkAdapter(OntologyFrameworkAdapterInterface ontologyFramework);
    public void setIoTMiddlewareAdapter(IoTMiddlewareAdapterInterface ioTMiddlewareAdapter);
    public void setMQTTBrokerAdapter(MQTTBrokerAdapterInterface mqttBrokerAdapter);

}
