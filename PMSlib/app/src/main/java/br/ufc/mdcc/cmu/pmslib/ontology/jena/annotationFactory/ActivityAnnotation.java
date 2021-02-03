package com.example.testanotationontology;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;

public class AtividadeAnnotation implements TypeSensoresAnnotation{
    SensorInterface sensor;
    String type;
    GenericAnnotation annotation = new GenericAnnotation();
    public AtividadeAnnotation(SensorInterface sensor){
        this.sensor = sensor;
    }
    @Override
    public OntModel sensorAnnotation() {
        annotation.createPrefix("iot-lite", "http://purl.oclc.org/NET/UNIS/fiware/iot-lite/");
        annotation.createPrefix("ssn", "http://purl.oclc.org/NET/ssnx/ssn/");
        Individual sensorAnot=annotation.createIndividual("ssn",
                "Sensor", "iot-lite", sensor.getId());
        Individual metadata= annotation.createIndividual("iot-lite", "Metadata-"+this.sensor.getId(),
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

        metadata =annotation.anotationDataProperty(metadata,"iot-lite",
                "Metadatatype",type);
        metadata=annotation.anotationDataProperty(metadata,"iot-lite",
                "Metavalue",num);
        sensorAnot=annotation.anotationObjectyProperty("iot-lite",sensorAnot, metadata, "hasMetadata");

        return annotation.returnModel();
    }

    @Override
    public void writeRDF(OntModel model) {
        annotation.writeRdf(model);
    }
}
