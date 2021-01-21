package br.ufc.mdcc.cmu.pmslib.ontology.jena;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.VCARD;

import java.io.File;
import java.io.FileWriter;

import br.ufc.mdcc.cmu.pmslib.exception.OntologyFrameworkException;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.SensorInterface;
import br.ufc.mdcc.cmu.pmslib.ontology.OntologyFrameworkAdapterInterface;

public final class OntologyFrameworkAdapterImpl extends OntologyFrameworkAdapterInterface {

    private String TAG = getClass().getName();
    private final int MY_PERMISSION_STORAGE_W = 222;
    private final int MY_PERMISSION_STORAGE_R = 333;
    private Resource sensor = null;
    private File file = null;

    private Model model = null;

    public OntologyFrameworkAdapterImpl(Context context) {
        super(context);

        this.file = new File(getContext().getFilesDir(), "RDF");

        // some definitions
        String sensorURI    = "http://health/sensor";
        String sensorType   = "GPS"; //Search in ontology the value sensor.getId();

        // create an empty Model
        this.model = ModelFactory.createDefaultModel();

        // create the resource
        this.sensor = model.createResource(sensorURI);

        // add the property
        this.sensor.addProperty(VCARD.FN, sensorType); //I am using FN as a temporary way

        /*Request permission*/
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_STORAGE_W
            );
            return;
        }
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSION_STORAGE_R
            );
            return;
        }
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
        Log.d(TAG, ">> stop ontology framework");
    }

    @Override
    public Object semanticAnnotation(SensorInterface sensor) {
        Log.d(TAG, ">> Semantic annotation OK!");

        this.sensor.addProperty(VCARD.N, sensor.getValue().get(0).toString()); //Lat
        this.sensor.addProperty(VCARD.ADR, sensor.getValue().get(1).toString()); //Long

        //this.sensor.getProperty(VCARD.FN).getString();
        //Log.d(TAG, ">> value "+this.sensor.getProperty(VCARD.FN).getString());
        return this.sensor;
    }

    @Override
    public File getRDF(Object object) {
        Log.d(TAG, ">> RDF file OK!");

        if (!file.exists()) {
            file.mkdir();
        }
        try {
            FileWriter writer = new FileWriter(file);
            model.write(writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
