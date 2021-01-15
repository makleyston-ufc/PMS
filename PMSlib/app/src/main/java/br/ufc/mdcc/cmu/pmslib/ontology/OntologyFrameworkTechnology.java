package br.ufc.mdcc.cmu.pmslib.ontology;

import android.content.Context;

import br.ufc.mdcc.cmu.pmslib.exception.OntologyFrameworkException;

public final class OntologyFrameworkTechnology extends OntologyFrameworkAdapterInterface {

    private static OntologyFrameworkTechnology instance = null;

    private Context context = null;

    private OntologyFrameworkAdapterInterface frameworkOntologyAdapter = null;

    private OntologyFrameworkTechnology(Context context){
        this.context = context;
    }

    public static OntologyFrameworkTechnology getInstance(Context context) {
        if(instance == null)
            instance = new OntologyFrameworkTechnology(context);
        return instance;
    }

    public OntologyFrameworkAdapterInterface getOntologyFrameworkAdapter() {
        return frameworkOntologyAdapter;
    }

    public void setOntologyFrameworkAdapter(OntologyFrameworkAdapterInterface ontologyFrameworkAdapter) {
        this.frameworkOntologyAdapter = ontologyFrameworkAdapter;
    }

    @Override
    public void loadKnowledge(String path) throws OntologyFrameworkException {
        this.frameworkOntologyAdapter.loadKnowledge(path);
    }

    @Override
    public void loadKnowledge(Object obg) throws OntologyFrameworkException {
        this.frameworkOntologyAdapter.loadKnowledge(obg);
    }

    @Override
    public void start() throws OntologyFrameworkException{
        this.frameworkOntologyAdapter.start();
    }

    @Override
    public void stop() throws OntologyFrameworkException{
        this.frameworkOntologyAdapter.stop();
    }

}
