package br.ufc.mdcc.cmu.pmslib.cep.resources;

import com.hp.hpl.jena.vocabulary.VCARD;

public class GPS extends Resource {

    public String lat;
    public String lng;

    public GPS(com.hp.hpl.jena.rdf.model.Resource resource) {
        super(resource);
        setLat(((com.hp.hpl.jena.rdf.model.Resource)resource).getProperty(VCARD.N).getString());
        setLng(((com.hp.hpl.jena.rdf.model.Resource)resource).getProperty(VCARD.ADR).getString());
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
