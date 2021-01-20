package br.ufc.mdcc.cmu.pmslib.ontology;

import android.content.Context;

import java.io.File;

import br.ufc.mdcc.cmu.pmslib.exception.OntologyFrameworkException;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.SensorInterface;

public final class OntologyFrameworkTechnology extends OntologyFrameworkAdapterInterface {

    private static OntologyFrameworkTechnology instance = null;

    private Context context = null;

    private OntologyFrameworkAdapterInterface ontologyFrameworkAdapter = null;

    private OntologyFrameworkTechnology(Context context){
        super(context);
        this.context = context;
    }

    public static OntologyFrameworkTechnology getInstance(Context context) {
        if(instance == null)
            instance = new OntologyFrameworkTechnology(context);
        return instance;
    }

    public OntologyFrameworkAdapterInterface getOntologyFrameworkAdapter() {
        return ontologyFrameworkAdapter;
    }

    public void setOntologyFrameworkAdapter(OntologyFrameworkAdapterInterface ontologyFrameworkAdapter) {
        this.ontologyFrameworkAdapter = ontologyFrameworkAdapter;
    }

    @Override
    public void loadKnowledge(String path) throws OntologyFrameworkException {
        this.ontologyFrameworkAdapter.loadKnowledge(path);
    }

    @Override
    public void loadKnowledge(Object obg) throws OntologyFrameworkException {
        this.ontologyFrameworkAdapter.loadKnowledge(obg);
    }

    @Override
    public void start() throws OntologyFrameworkException{
        this.ontologyFrameworkAdapter.start();
    }

    @Override
    public void stop() throws OntologyFrameworkException{
        this.ontologyFrameworkAdapter.stop();
    }

    @Override
    public Object semanticAnnotation(SensorInterface sensor) {
        return this.ontologyFrameworkAdapter.semanticAnnotation(sensor);
    }

    @Override
    public File getRDF(Object object) {
        return this.ontologyFrameworkAdapter.getRDF(object);
    }

}
