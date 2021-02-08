package br.ufc.mdcc.cmu.pmslib.ontology.jena.annotationFactory;

import com.hp.hpl.jena.ontology.OntModel;

import java.io.File;

public interface SensorTypeAnnotation {
    public OntModel sensorAnnotation();
    public File writeRDF(OntModel model);
}

