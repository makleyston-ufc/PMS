package com.example.testanotationontology;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;

public abstract class OntologyAnnotationFactory {

    public abstract void createPrefix(String name, String prefix);
    public abstract Individual createIndividual(String prefix, String name, String nameRecurso);
    public abstract Individual createIndividual(String prefixRecurso, String nameRecurso,String prefixIndividual, String individualName );
    public abstract Individual anotationObjectyProperty(String prefix, Individual a, Individual b, String property);
    public abstract Individual anotationDataProperty(Individual a, String prefix,  String property, Double value);
    public abstract Individual anotationDataProperty(Individual a, String prefix, String property, int value);
    public abstract Individual anotationDataProperty(Individual a, String prefix,  String property, String value);
    public abstract OntModel returnModel();
    public abstract void writeRdf(OntModel model);
}
