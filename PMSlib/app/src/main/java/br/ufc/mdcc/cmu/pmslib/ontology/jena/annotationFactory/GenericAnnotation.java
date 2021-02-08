package br.ufc.mdcc.cmu.pmslib.ontology.jena.annotationFactory;

import android.os.Environment;
import android.util.Log;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GenericAnnotation extends OntologyAnnotationFactory {
//    String TAG = "ANNOTATION";
    String TAG = getClass().getSimpleName();

    OntModel individualModel =ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
    OntModel propertyModel =ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

    @Override
    public void createPrefix(String name, String prefix) {
        this.individualModel.setNsPrefix(name, prefix);
    }

    @Override
    public Individual createIndividual(String prefix, String name, String nameRecurso) {
        Log.d(TAG, ">> Create individual");
        Resource r = this.propertyModel.createResource(this.individualModel.getNsPrefixURI(prefix)+nameRecurso);
        Individual individual = this.individualModel.createIndividual(this.individualModel.getNsPrefixURI(prefix)+name,r);
        return individual;
    }

    @Override
    public Individual createIndividual(String prefixRecurso, String nameRecurso, String prefixIndividual, String individualName) {
        Log.d(TAG, ">> Create individual");
        Resource r = this.propertyModel.createResource(this.individualModel.getNsPrefixURI(prefixRecurso)+nameRecurso);
        Individual individual = this.individualModel.createIndividual(this.individualModel.getNsPrefixURI(prefixIndividual)+individualName,r);
        return individual;
    }

    @Override
    public Individual annotationObjectProperty(String prefix, Individual a, Individual b, String property) {
        Log.d(TAG, ">> Annotation individual");
        ObjectProperty p = this.propertyModel.createObjectProperty(this.individualModel.getNsPrefixURI(prefix)+property);
        a.setPropertyValue(p,b);

        return a;
    }

    @Override
    public Individual annotationDataProperty(Individual a, String prefix, String property, Double value) {
        Log.d(TAG, ">> Annotation individual");
        DatatypeProperty d = this.propertyModel.createDatatypeProperty(this.individualModel.getNsPrefixURI(prefix)+property);

        a.setPropertyValue(d, propertyModel.createTypedLiteral(value));
        return a;
    }

    @Override
    public Individual annotationDataProperty(Individual a, String prefix, String property, int value) {
        Log.d(TAG, ">> Annotation individual");
        DatatypeProperty d = this.propertyModel.createDatatypeProperty(this.individualModel.getNsPrefixURI(prefix)+property);

        a.setPropertyValue(d, propertyModel.createTypedLiteral(value));
        return a;
    }

    @Override
    public Individual annotationDataProperty(Individual a, String prefix, String property, String value) {
        Log.d(TAG, ">> Annotation individual");
        DatatypeProperty d = this.propertyModel.createDatatypeProperty(this.individualModel.getNsPrefixURI(prefix)+property);
        a.setPropertyValue(d, propertyModel.createTypedLiteral(value));
        return a;
    }

    @Override
    public File writeRdf(OntModel model) {

        Log.d(TAG, ">> Write RDF");
//        model.write(System.out, "RDF/XML");

        File file = new File(Environment.getExternalStorageDirectory() + "/" + File.separator + "model.ref");
        try {

            file.createNewFile();
            if(file.exists())
            {
                FileWriter writer = new FileWriter(file);
                model.write(writer, "RDF/XML");
                writer.close();

                Log.d(TAG, ">> RDF file created successfully!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;

    }

    @Override
    public OntModel returnModel() {
        Log.d(TAG, ">> Return Model");
        return this.individualModel;
    }

}
