package com.example.testanotationontology;

import com.hp.hpl.jena.ontology.OntModel;

public interface TypeSensoresAnnotation {
    public OntModel sensorAnnotation();
    public void writeRDF(OntModel model);
}

