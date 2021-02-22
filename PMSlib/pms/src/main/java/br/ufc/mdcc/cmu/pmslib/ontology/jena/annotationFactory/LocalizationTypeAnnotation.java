package br.ufc.mdcc.cmu.pmslib.ontology.jena.annotationFactory;

import android.util.Log;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;

import java.io.File;

import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.SensorInterface;

public class LocalizationTypeAnnotation implements SensorsTypeAnnotation {
    SensorInterface sensor;
    GenericAnnotation annotation = new GenericAnnotation();
    public LocalizationTypeAnnotation(SensorInterface sensor){
        this.sensor = sensor;
    }
    public OntModel sensorAnnotation(){
        annotation.createPrefix("iot-lite", "http://purl.oclc.org/NET/UNIS/fiware/iot-lite/");
        annotation.createPrefix("ssn", "http://purl.oclc.org/NET/ssnx/ssn/");
        annotation.createPrefix("geo", "http://www.w3.org/2003/01/geo/wgs84_pos/");
        Individual sensorAnot=annotation.createIndividual("ssn",
                "LocationSensor", "iot-lite", sensor.getId());
        Individual point= annotation.createIndividual("geo", "Point",
                this.sensor.getType());
        point =annotation.annotationDataProperty(point,"geo",
                "lat",this.sensor.getValue().get(0));
        point =annotation.annotationDataProperty(point,"geo",
                "long",this.sensor.getValue().get(1));
        sensorAnot=annotation.annotationObjectProperty("geo",sensorAnot, point, "location");

        return annotation.returnModel();
    }
    public File writeRDF(OntModel model){
        return annotation.writeRdf(model);
    }

    @Override
    public double returnValuePropertyDouble(String uri, String name) {
        return annotation.returnValuePropertyDouble(uri, name);
    }

    @Override
    public String returnValuePropertyString(String uri, String name) {
        return annotation.returnValuePropertyString(uri, name);
    }

    @Override
    public int returnValuePropertyInt(String uri, String name) {
        return annotation.returnValuePropertyInt(uri, name);
    }

    @Override
    public String returnIdSensor() {
        return annotation.returnIdSensor();
    }
}
