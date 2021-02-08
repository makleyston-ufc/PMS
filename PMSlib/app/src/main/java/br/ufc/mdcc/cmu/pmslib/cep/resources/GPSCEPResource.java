package br.ufc.mdcc.cmu.pmslib.cep.resources;

import android.content.Context;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.vocabulary.VCARD;

import br.ufc.mdcc.cmu.pmslib.cep.CEPResource;

public class GPSCEPResource extends CEPResource {

    public String lat;
    public String lng;

    public GPSCEPResource(OntModel ontModel, Context context) {
        super(ontModel, context);
        addDomain("/teste");

        DatatypeProperty d = ontModel.getNsPrefixURI("geo")+"lat";
        setLat(ontModel.getDatatypeProperty(ontModel.getNsPrefixURI("geo")+"lat").getProperty(d));
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

    @Override
    public String getStatement() {
        String stm = "select * from GPSCEPResource(lat='-3.7710616')";
        return stm;
    }
}
