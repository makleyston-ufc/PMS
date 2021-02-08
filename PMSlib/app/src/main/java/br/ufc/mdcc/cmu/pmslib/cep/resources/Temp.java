package br.ufc.mdcc.cmu.pmslib.cep.resources;

import android.content.Context;

import com.hp.hpl.jena.ontology.OntModel;

import br.ufc.mdcc.cmu.pmslib.cep.CEPResource;

public class Temp extends CEPResource {
    public Temp(OntModel ontModel, Context context) {
        super(ontModel, context);
    }

    @Override
    public String getStatement() {
        return null;
    }
}
