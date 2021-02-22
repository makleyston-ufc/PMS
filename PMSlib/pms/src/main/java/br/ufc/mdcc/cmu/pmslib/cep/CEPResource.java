package br.ufc.mdcc.cmu.pmslib.cep;

import com.hp.hpl.jena.ontology.OntModel;

import br.ufc.mdcc.cmu.pmslib.exception.SemanticAnnotationException;
import br.ufc.mdcc.cmu.pmslib.ontology.jena.annotationFactory.GenericAnnotation;

public abstract class CEPResource extends StatementSubscriber {

    private String id;
    private String type;
    private GenericAnnotation genericAnnotation = null;

    OntModel ontModel = null;
    private final String TAG = getClass().getSimpleName();

    public CEPResource(Object ontModel) {
        super();
        if(ontModel instanceof OntModel) {
            this.ontModel = (OntModel) ontModel;
            this.genericAnnotation = new GenericAnnotation();
            this.genericAnnotation.setOntModel((OntModel) ontModel);
        }else{
            this.configCEPResource(new ConfigCEPResource());
        }
    }

    public GenericAnnotation getSemanticAnnotation() throws SemanticAnnotationException {
        if(this.genericAnnotation != null)
            return this.genericAnnotation;
        else
            throw new SemanticAnnotationException();
    }

    public OntModel getOntModel(){
        return this.ontModel;
    }

    public abstract void configCEPResource(ConfigCEPResource configCEPResource);

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return this.id;
    }

    public String getType() {
        return type;
    }

    public void setType(String vocabulary, String type) {
        this.type = vocabulary + type;
    }

    public class ConfigCEPResource {
        public void addTopic(String topic){
            CEPResource.this.addTopic(topic);
        }

        public void setType(String vocabulary, String type){
            CEPResource.this.setType(vocabulary, type);
        }
    }

}

