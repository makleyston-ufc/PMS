package br.ufc.mdcc.cmu.pmslib.ontology;

import android.content.Context;

import java.io.File;

import br.ufc.mdcc.cmu.pmslib.exception.OntologyFrameworkException;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.SensorInterface;

public abstract class OntologyFrameworkAdapterInterface{

    private Context context = null;
    public OntologyFrameworkAdapterInterface(Context context){
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public abstract void loadKnowledge(String path) throws OntologyFrameworkException;

    public abstract void loadKnowledge(Object obg) throws OntologyFrameworkException;

    public abstract void start() throws OntologyFrameworkException;

    public abstract void stop() throws OntologyFrameworkException;

    public abstract Object semanticAnnotation(SensorInterface sensor);

    public abstract File getRDF(Object object);

    public abstract void requestPermissions();

}
