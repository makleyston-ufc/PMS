package br.ufc.mdcc.cmu.pmslib.cep.resources;

import android.util.Log;

import com.hp.hpl.jena.ontology.OntModel;
import br.ufc.mdcc.cmu.pmslib.cep.CEPResource;
import br.ufc.mdcc.cmu.pmslib.ontology.jena.annotationFactory.GenericAnnotation;

public class GPSCEPResource extends CEPResource {

    public String lat;
    public String lng;

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

    public GPSCEPResource(){
        super();
        addDomain("/test");
        setId("LocalizationSensor");
    }

    public GPSCEPResource(OntModel ontModel) {
        super(ontModel);
        GenericAnnotation genericAnnotation = new GenericAnnotation();
        genericAnnotation.setOntModel(ontModel);
        lat = genericAnnotation.returnValuePropertyString("http://www.w3.org/2003/01/geo/wgs84_pos/","lat");
        lng = genericAnnotation.returnValuePropertyString("http://www.w3.org/2003/01/geo/wgs84_pos/","long");

        Log.d(TAG, ">> Lat: "+lat);
        Log.d(TAG, ">> Lng: "+lng);
    }

    @Override
    public String getStatement() {
        String stm = "select o from GPSCEPResource as o where o.lat = '-3.7710643'";
        return stm;
    }

}
