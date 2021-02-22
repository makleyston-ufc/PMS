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
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class GenericAnnotation extends OntologyAnnotationFactory {
    String TAG = getClass().getSimpleName();
    OntModel individualModel =ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
    OntModel propertyModel =ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

    public void setOntModel(OntModel ontModel){
        this.individualModel = ontModel;
    }

    @Override
    public void createPrefix(String name, String prefix) {
        this.individualModel.setNsPrefix(name, prefix);
    }

    @Override
    public Individual createIndividual(String prefix, String name, String nameRecurso) {
        Log.d(TAG, ">> Individual Create");
        Resource r = this.propertyModel.createResource(this.individualModel.getNsPrefixURI(prefix)+nameRecurso);
        Individual individual = this.individualModel.createIndividual(this.individualModel.getNsPrefixURI(prefix)+name,r);
        return individual;
    }

    @Override
    public Individual createIndividual(String prefixRecurso, String nameRecurso, String prefixIndividual, String individualName) {
        Log.d(TAG, ">> Individual Create");
        Resource r = this.propertyModel.createResource(this.individualModel.getNsPrefixURI(prefixRecurso)+nameRecurso);
        Individual individual = this.individualModel.createIndividual(this.individualModel.getNsPrefixURI(prefixIndividual)+individualName,r);
        return individual;
    }

    @Override
    public Individual annotationObjectProperty(String prefix, Individual a, Individual b, String property) {
        Log.d(TAG, ">> Individual Annotation");
        ObjectProperty p = this.propertyModel.createObjectProperty(this.individualModel.getNsPrefixURI(prefix)+property);
        a.setPropertyValue(p,b);

        return a;
    }

    @Override
    public Individual annotationDataProperty(Individual a, String prefix, String property, Double value) {
        Log.d(TAG, ">> Individual Annotation");
        DatatypeProperty d = this.propertyModel.createDatatypeProperty(this.individualModel.getNsPrefixURI(prefix)+property);

        a.setPropertyValue(d, propertyModel.createTypedLiteral(value));
        return a;
    }

    @Override
    public Individual annotationDataProperty(Individual a, String prefix, String property, int value) {
        Log.d(TAG, ">> Individual Annotation");
        DatatypeProperty d = this.propertyModel.createDatatypeProperty(this.individualModel.getNsPrefixURI(prefix)+property);

        a.setPropertyValue(d, propertyModel.createTypedLiteral(value));
        return a;
    }

    @Override
    public Individual annotationDataProperty(Individual a, String prefix, String property, String value) {
        Log.d(TAG, ">> Individual Annotation");
        DatatypeProperty d = this.propertyModel.createDatatypeProperty(this.individualModel.getNsPrefixURI(prefix)+property);
        a.setPropertyValue(d, propertyModel.createTypedLiteral(value));
        return a;
    }

    @Override
    public File writeRdf(OntModel model) {
        Log.d(TAG, ">> Write RDF");

        File file = new File(Environment.getExternalStorageDirectory() + "/" + File.separator + "model.rdf");
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
    public double returnValuePropertyDouble(String uri, String name) {
        for (StmtIterator it = this.individualModel.listStatements(); it.hasNext(); ) {
            Statement s = it.next();
            if (s.getPredicate().toString().equals(uri + name)) {
                String[] parts=s.getObject().toString().split(Pattern.quote("^"));
//                Log.d(TAG, s.getObject().toString());
//                Log.d(TAG, parts[0]);
                return Double.parseDouble(parts[0]);
            }
        }
        return 0;
    }

    @Override
    public String returnValuePropertyString(String uri, String name) {
        for (StmtIterator it = this.individualModel.listStatements(); it.hasNext(); ) {
            Statement s = it.next();

            if (s.getPredicate().toString().equals(uri + name)) {
                String[] parts=s.getObject().toString().split(Pattern.quote("^"));
//                Log.d(TAG, s.getObject().toString());
//                Log.d(TAG, parts[0]);
                return parts[0];
            }
        }
        return null;
    }

    @Override
    public int returnValuePropertyInt(String uri, String name) {
        for (StmtIterator it = this.individualModel.listStatements(); it.hasNext(); ) {
            Statement s = it.next();
            if (s.getPredicate().toString().equals(uri + name)) {
                String[] parts=s.getObject().toString().split(Pattern.quote("^"));
//                Log.d(TAG, s.getObject().toString());
//                Log.d(TAG, parts[0]);
                return Integer.parseInt(parts[0]);
            }
        }
        return 0;
    }

    @Override
    public OntModel returnModel() {
        Log.d(TAG, ">> Return Model");
        return this.individualModel;
    }

    @Override
    public String returnIdSensor() {
        for (StmtIterator it = this.individualModel.listStatements(); it.hasNext(); ) {
            Statement s = it.next();
            if (s.getPredicate().toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") && s.getObject().toString().equals("http://purl.oclc.org/NET/ssnx/ssn/Sensor")) {
                String subject = s.getSubject().getLocalName();
                Log.d(TAG, subject);
                return subject;
            }
        }
        return null;
    }


    public String returnTypeSensor() {
        return this.returnValuePropertyString("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "type").trim();
    }
}
