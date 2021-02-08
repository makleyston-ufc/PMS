package br.ufc.mdcc.cmu.pmslib.ontology.jena;

import android.content.Context;
import android.util.Log;

import com.hp.hpl.jena.ontology.OntModel;

import java.io.File;

import br.ufc.mdcc.cmu.pmslib.exception.OntologyFrameworkException;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.SensorInterface;
import br.ufc.mdcc.cmu.pmslib.ontology.OntologyFrameworkAdapterInterface;
import br.ufc.mdcc.cmu.pmslib.ontology.jena.annotationFactory.ActivityAnnotationSensor;
import br.ufc.mdcc.cmu.pmslib.ontology.jena.annotationFactory.GenericAnnotation;
import br.ufc.mdcc.cmu.pmslib.ontology.jena.annotationFactory.LocalizationAnnotationSensor;

public final class OntologyFrameworkAdapterImpl extends OntologyFrameworkAdapterInterface {

    private String TAG = getClass().getName();

    public OntologyFrameworkAdapterImpl(Context context) {
        super(context);
    }

    @Override
    public void loadKnowledge(String path) throws OntologyFrameworkException {
        Log.d(TAG, ">> loadKnowledge string");
        //TODO
    }

    @Override
    public void loadKnowledge(Object obg) throws OntologyFrameworkException {
        Log.d(TAG, ">> loadKnowledge obj");
        //TODO
    }

    @Override
    public void start() {
        Log.d(TAG, ">> start ontology framework");
        //TODO
    }

    @Override
    public void stop() {
        Log.d(TAG, ">> stop ontology framework");}

    @Override
    public Object semanticAnnotation(SensorInterface sensor) {
        Log.d(TAG, ">> Semantic annotation of sensor read");
        if(sensor.getId()=="LocalizationSensor"){
            LocalizationAnnotationSensor localizationSensor =new LocalizationAnnotationSensor(sensor);
            return localizationSensor.sensorAnnotation();
        }else if(sensor.getId()=="ActivitySensor") {
            ActivityAnnotationSensor sensorActivity = new ActivityAnnotationSensor(sensor);
            return sensorActivity.sensorAnnotation();
        }
        return null;
    }

    @Override
    public File getRDF(Object object) {
        Log.d(TAG, ">> Generate RDF file with Object that references an individual in the Ontology");
        GenericAnnotation gen = new GenericAnnotation();
        return gen.writeRdf((OntModel) object);

    }

    @Override
    public void requestPermissions() {
        //TODO
    }

}
