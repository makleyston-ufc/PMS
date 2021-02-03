package com.example.testanotationontology;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class LocalizationAnnotation implements TypeSensoresAnnotation{
    SensorInterface sensor;
    GenericAnnotation annotation = new GenericAnnotation();
    public LocalizationAnnotation(SensorInterface sensor){
        this.sensor = sensor;
    }
    public OntModel sensorAnnotation(){
        annotation.createPrefix("iot-lite", "http://purl.oclc.org/NET/UNIS/fiware/iot-lite/");
        annotation.createPrefix("ssn", "http://purl.oclc.org/NET/ssnx/ssn/");
        annotation.createPrefix("geo", "http://www.w3.org/2003/01/geo/wgs84_pos/");
        Individual sensorAnot=annotation.createIndividual("ssn",
                "Sensor", "iot-lite", sensor.getId());
        Individual point= annotation.createIndividual("geo", "Point",
                "Location-"+this.sensor.getId());
        point =annotation.anotationDataProperty(point,"geo",
                "lat",this.sensor.getValue().get(0));
        point =annotation.anotationDataProperty(point,"geo",
                "long",this.sensor.getValue().get(1));
        sensorAnot=annotation.anotationObjectyProperty("geo",sensorAnot, point, "location");

        return annotation.returnModel();
    }
    public void writeRDF(OntModel model){
        annotation.writeRdf(model);
    }
    //propriedade iot-lite http://purl.oclc.org/NET/UNIS/fiware/iot-lite
    //proprieda geo http://www.w3.org/2003/01/geo/wgs84_pos
    //instance-iot-lite http://purl.oclc.org/NET/UNIS/iot-lite/iot-liteInstance#

}
