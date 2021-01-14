package br.ufc.mdcc.cmu.pmslib.ontology;

import android.content.Context;
import android.util.Log;

import br.ufc.mdcc.cmu.pmslib.exception.OntologyFrameworkException;

public final class OntologyFrameworkAdapterImpl extends OntologyFrameworkAdapterInterface {

    private String TAG = getClass().getName();

    public OntologyFrameworkAdapterImpl() {}

    @Override
    public void loadKnowledge(String path) throws OntologyFrameworkException {
        Log.d(TAG, ">> loadKnowledge string");
    }

    @Override
    public void loadKnowledge(Object obg) throws OntologyFrameworkException {
        Log.d(TAG, ">> loadKnowledge obg");
    }

    @Override
    public void start() {
        Log.d(TAG, ">> start ontology framework");
    }

    @Override
    public void stop() {
        Log.d(TAG, ">> stop ontology framework");
    }

}
