package br.ufc.mdcc.cmu.pmslib.ontology;

import android.content.Context;
import android.util.Log;

import java.io.File;

import br.ufc.mdcc.cmu.pmslib.exception.OntologyFrameworkException;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.SensorInterface;

public final class OntologyFrameworkAdapterImpl extends OntologyFrameworkAdapterInterface {

    private String TAG = getClass().getName();

    public OntologyFrameworkAdapterImpl(Context context) {
        super(context);
    }

    @Override
    public void loadKnowledge(String path) throws OntologyFrameworkException {
        Log.d(TAG, ">> loadKnowledge string");
        //TODO
    }

    @Override
    public void loadKnowledge(Object obg) throws OntologyFrameworkException {
        Log.d(TAG, ">> loadKnowledge obj");
        //TODO
    }

    @Override
    public void start() {
        Log.d(TAG, ">> start ontology framework");
        //TODO
    }

    @Override
    public void stop() {
        Log.d(TAG, ">> stop ontology framework");
    }

    @Override
    public Object semanticAnnotation(SensorInterface sensor) {
        Log.d(TAG, ">> Semantic annotation of sensor read");
        //TODO
        return null;
    }

    @Override
    public File getRDF(Object object) {
        Log.d(TAG, ">> Generate RDF file with Object that references an individual in the Ontology");
        //TODO
        return null;
    }

}
