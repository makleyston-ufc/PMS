package br.ufc.mdcc.cmu.pmslib.cep;

import android.content.Context;

import com.hp.hpl.jena.ontology.OntModel;

import br.ufc.mdcc.cmu.pmslib.cep.StatementSubscriber;

public abstract class CEPResource extends StatementSubscriber {

    OntModel ontModel = null;
    public CEPResource(OntModel ontModel, Context context){
        super(context);
        this.ontModel = ontModel;
    }

}
