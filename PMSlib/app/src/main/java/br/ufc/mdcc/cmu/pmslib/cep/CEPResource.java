package br.ufc.mdcc.cmu.pmslib.cep;

import com.hp.hpl.jena.ontology.OntModel;

public abstract class CEPResource extends StatementSubscriber {

    private String id;

    OntModel ontModel = null;
    public CEPResource(OntModel ontModel) {
        super();
        this.ontModel = ontModel;
    }

    public OntModel getOntModel(){
        return this.ontModel;
    }

    public CEPResource(){
        super();
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return this.id;
    }

}
