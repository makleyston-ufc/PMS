package br.ufc.mdcc.cmu.pmslib.cep.resources;

import com.hp.hpl.jena.ontology.OntModel;
import br.ufc.mdcc.cmu.pmslib.cep.CEPResource;
import br.ufc.mdcc.cmu.pmslib.exception.SemanticAnnotationException;

public class GPSCEPResource extends CEPResource {

    public String lat;
    public String lng;

    public GPSCEPResource(OntModel ontModel) {
        super(ontModel);
        try {
            setLat(getSemanticAnnotation().returnValuePropertyString("http://www.w3.org/2003/01/geo/wgs84_pos/","lat"));
            setLng(getSemanticAnnotation().returnValuePropertyString("http://www.w3.org/2003/01/geo/wgs84_pos/","long"));
        } catch (SemanticAnnotationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void configCEPResource(ConfigCEPResource configCEPResource) {
        configCEPResource.addTopic("/test");
        configCEPResource.setType("http://www.w3.org/2003/01/geo/wgs84_pos/", "LocalizationSensor");
    }

    @Override
    public String getStatement() {
        String stm = "select o from GPSCEPResource as o where o.lat = '-3.771074'";
        return stm;
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

    final String TAG = getClass().getSimpleName();

}
