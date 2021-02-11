package br.ufc.mdcc.cmu.pmslib.cep;

import com.hp.hpl.jena.ontology.OntModel;

public abstract class CEPResource extends StatementSubscriber {

    private String id;
    private String type;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = "http://www.w3.org/2003/01/geo/wgs84_pos/"+type;
    }

    public void setType(String vocabulary, String type) {
        this.type = vocabulary + type;
    }
}
