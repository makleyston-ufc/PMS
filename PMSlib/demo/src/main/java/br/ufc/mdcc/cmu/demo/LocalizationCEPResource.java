package br.ufc.mdcc.cmu.demo;

import android.util.Log;

import br.ufc.mdcc.cmu.pmslib.cep.CEPResource;
import br.ufc.mdcc.cmu.pmslib.exception.SemanticAnnotationException;

public class LocalizationCEPResource extends CEPResource {

    public String lat;

    private final String vocabulary = "http://www.w3.org/2003/01/geo/wgs84_pos/";

    public LocalizationCEPResource(Object ontModel){
        super(ontModel);
        try {
            setLat(getSemanticAnnotation().returnValuePropertyString(this.vocabulary,"lat"));
        } catch (SemanticAnnotationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void configCEPResource(ConfigCEPResource configCEPResource) {
        configCEPResource.addTopic("/J10");
        configCEPResource.setType(this.vocabulary, "LocalizationSensor");
    }

    @Override
    public String getStatement() {
        return "select o from LocalizationCEPResource as o where o.lat = '-3.7710447'";
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
}
