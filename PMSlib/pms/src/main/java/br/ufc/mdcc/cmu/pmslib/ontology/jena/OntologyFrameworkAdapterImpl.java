package br.ufc.mdcc.cmu.pmslib.ontology.jena;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

import br.ufc.mdcc.cmu.pmslib.cep.CEPResource;
import br.ufc.mdcc.cmu.pmslib.exception.OntologyFrameworkException;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.SensorInterface;
import br.ufc.mdcc.cmu.pmslib.ontology.OntologyFrameworkAdapterInterface;
import br.ufc.mdcc.cmu.pmslib.ontology.jena.annotationFactory.ActivityTypeAnnotation;
import br.ufc.mdcc.cmu.pmslib.ontology.jena.annotationFactory.GenericAnnotation;
import br.ufc.mdcc.cmu.pmslib.ontology.jena.annotationFactory.LocalizationTypeAnnotation;

public final class OntologyFrameworkAdapterImpl extends OntologyFrameworkAdapterInterface {

    private String TAG = getClass().getName();

    private final int MY_PERMISSION_STORAGE_W = 222;
    private final int MY_PERMISSION_STORAGE_R = 333;
    public OntologyFrameworkAdapterImpl(Context context) {
        super(context);
    }

    @Override
    public void loadKnowledge(String path) throws OntologyFrameworkException {
        Log.d(TAG, ">> loadKnowledge string");
        //TODOl
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

        /*Rever uma forma de resolver a heterogeneidade!*/
    @Override
    public Object semanticAnnotation(SensorInterface sensor) {
        Log.d(TAG, ">> Semantic annotation of sensor read");
        if(sensor.getFullType().trim().equals("http://www.w3.org/2003/01/geo/wgs84_pos/LocalizationSensor")){
            LocalizationTypeAnnotation localizationSensor = new LocalizationTypeAnnotation(sensor);
            return localizationSensor.sensorAnnotation();
        }else if(sensor.getType()=="ActivitySensor") {
            ActivityTypeAnnotation activitySensor = new ActivityTypeAnnotation(sensor);
            return activitySensor.sensorAnnotation();
        }
        return null;
    }

    @Override
    public File getRDF(Object object) {
        Log.d(TAG, ">> Generate RDF file with Object that references an individual in the Ontology");
        GenericAnnotation gen = new GenericAnnotation();
        return gen.writeRdf(((CEPResource) object).getOntModel());
    }

    @Override
    public void requestPermissions() {
        /*Request permission*/
        if (ContextCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    (Activity) getContext(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_STORAGE_W
            );
            return;
        }
        if (ContextCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    (Activity) getContext(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSION_STORAGE_R
            );
            return;
        }
    }

}
