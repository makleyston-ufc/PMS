package br.ufc.mdcc.cmu.pmslib.ontology;

import br.ufc.mdcc.cmu.pmslib.exception.OntologyFrameworkException;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.SensorInterface;

public abstract class OntologyFrameworkAdapterInterface{

    public abstract void loadKnowledge(String path) throws OntologyFrameworkException;

    public abstract void loadKnowledge(Object obg) throws OntologyFrameworkException;

    public abstract void start() throws OntologyFrameworkException;

    public abstract void stop() throws OntologyFrameworkException;

    public abstract Object semanticAnnotation(SensorInterface sensor);

}
