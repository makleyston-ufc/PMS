package br.ufc.mdcc.cmu.pmslib.ontology.jena.annotationFactory;

import android.util.Log;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;

import java.io.File;

import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.SensorInterface;

public class UserLocalizationTypeAnnotation implements SensorsTypeAnnotation {
    SensorInterface sensor;
    String cpf;
    String data, time;
    GenericAnnotation annotation = new GenericAnnotation();
    public UserLocalizationTypeAnnotation(SensorInterface sensor, String cpf, String data, String time){
        this.sensor = sensor;
        this.cpf=cpf;
        this.data = data;
        this.time =time;
    }
    public OntModel sensorAnnotation(){
        annotation.createPrefix("iot-lite", "http://purl.oclc.org/NET/UNIS/fiware/iot-lite/");
        annotation.createPrefix("ssn", "http://purl.oclc.org/NET/ssnx/ssn/");
        annotation.createPrefix("geo", "http://www.w3.org/2003/01/geo/wgs84_pos/");
        annotation.createPrefix("foaf", "http://xmlns.com/foaf/0.1/");
        annotation.createPrefix("pms", "http://www.pmsexample.com/");
        Individual sensorAnot=annotation.createIndividual("ssn",
                "LocationSensor", "iot-lite", sensor.getId());
        Individual point= annotation.createIndividual("geo", "Point",
                this.sensor.getType());
        Individual user= annotation.createIndividual("pms",this.cpf, "foaf"
                "Person");
        point =annotation.annotationDataProperty(point,"geo",
                "lat",this.sensor.getValue().get(0));
        point =annotation.annotationDataProperty(point,"geo",
                "long",this.sensor.getValue().get(1));
        point =annotation.annotationDataProperty(point,"pms",
                "day",this.data);
        point =annotation.annotationDataProperty(point,"pms",
                "time",this.time);
        sensorAnot=annotation.annotationObjectProperty("geo",sensorAnot, point, "location");
        sensorAnot=annotation.annotationObjectProperty("pms",sensorAnot, user, "hasProprietary");
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
