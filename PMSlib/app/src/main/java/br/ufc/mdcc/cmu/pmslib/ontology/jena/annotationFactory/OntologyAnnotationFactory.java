package br.ufc.mdcc.cmu.pmslib.ontology.jena.annotationFactory;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;

import java.io.File;

public abstract class OntologyAnnotationFactory {

    public abstract void createPrefix(String name, String prefix);
    public abstract Individual createIndividual(String prefix, String name, String nameRecurso);
    public abstract Individual createIndividual(String prefixRecurso, String nameRecurso,String prefixIndividual, String individualName );
    public abstract Individual annotationObjectProperty(String prefix, Individual a, Individual b, String property);
    public abstract Individual annotationDataProperty(Individual a, String prefix,  String property, Double value);
    public abstract Individual annotationDataProperty(Individual a, String prefix, String property, int value);
    public abstract Individual annotationDataProperty(Individual a, String prefix,  String property, String value);
    public abstract OntModel returnModel();
    public abstract File writeRdf(OntModel model);
}
