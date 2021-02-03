package br.ufc.mdcc.cmu.pmslib.cep.resources;

public abstract class Resource {

    com.hp.hpl.jena.rdf.model.Resource resource = null;
    public Resource(com.hp.hpl.jena.rdf.model.Resource resource){
        this.resource = resource;
    }

}
