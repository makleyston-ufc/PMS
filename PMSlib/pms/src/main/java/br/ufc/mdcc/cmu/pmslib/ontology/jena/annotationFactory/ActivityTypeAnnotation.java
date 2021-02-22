package br.ufc.mdcc.cmu.pmslib.ontology.jena.annotationFactory;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;

import java.io.File;

import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.SensorInterface;

public class ActivityTypeAnnotation implements SensorsTypeAnnotation {
    SensorInterface sensor;
    String type;
    GenericAnnotation annotation = new GenericAnnotation();
    public ActivityTypeAnnotation(SensorInterface sensor){
        this.sensor = sensor;
    }
    @Override
    public OntModel sensorAnnotation() {
        annotation.createPrefix("iot-lite", "http://purl.oclc.org/NET/UNIS/fiware/iot-lite/");
        annotation.createPrefix("ssn", "http://purl.oclc.org/NET/ssnx/ssn/");
        Individual sensorAnot=annotation.createIndividual("ssn",
                "Sensor", "iot-lite", sensor.getId());
        Individual metadata= annotation.createIndividual("iot-lite", "Metadata-"+this.sensor.getType(),
                "Metadata");
        int num = Integer.valueOf(this.sensor.getValue().get(0).intValue());
        switch (num){
            case 0:
                type = "No veículo";
                break;
            case 1:
                type = "Na bicicleta";
                break;
            case 2:
                type = "À pé";
                break;
            case 3:
                type = "Parado";
                break;
            case 4:
                type = "Desconhecido";
                break;
            case 5:
                type = "Inclinado";
                break;
            case 7:
                type = "Caminhando";
                break;
            case 8:
                type = "Correndo";
                break;
        }

        metadata =annotation.annotationDataProperty(metadata,"iot-lite",
                "Metadatatype",type);
        metadata=annotation.annotationDataProperty(metadata,"iot-lite",
                "Metavalue",num);
        sensorAnot=annotation.annotationObjectProperty("iot-lite",sensorAnot, metadata, "hasMetadata");

        return annotation.returnModel();
    }

    @Override
    public File writeRDF(OntModel model) {
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
