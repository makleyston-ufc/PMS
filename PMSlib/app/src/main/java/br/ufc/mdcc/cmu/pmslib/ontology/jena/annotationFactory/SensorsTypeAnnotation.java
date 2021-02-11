package br.ufc.mdcc.cmu.pmslib.ontology.jena.annotationFactory;

import com.hp.hpl.jena.ontology.OntModel;

import java.io.File;

public interface SensorsTypeAnnotation {
    public OntModel sensorAnnotation();
    public File writeRDF(OntModel model);
    public double returnValuePropertyDouble(String uri, String name);
    public String returnValuePropertyString(String uri, String name);
    public int returnValuePropertyInt(String uri, String name);
    public String returnIdSensor();
}

