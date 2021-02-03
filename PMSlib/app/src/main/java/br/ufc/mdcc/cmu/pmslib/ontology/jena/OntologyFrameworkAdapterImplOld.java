package br.ufc.mdcc.cmu.pmslib.ontology.jena;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.vocabulary.VCARD;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import br.ufc.mdcc.cmu.pmslib.exception.OntologyFrameworkException;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.SensorInterface;
import br.ufc.mdcc.cmu.pmslib.ontology.OntologyFrameworkAdapterInterface;

public final class OntologyFrameworkAdapterImplOld extends OntologyFrameworkAdapterInterface {

    private String TAG = getClass().getSimpleName();
    private final int MY_PERMISSION_STORAGE_W = 222;
    private final int MY_PERMISSION_STORAGE_R = 333;
    private Resource sensor = null;

    private Model model = null;

    public OntologyFrameworkAdapterImpl(Context context) {
        super(context);

        // some definitions
        String sensorURI    = "http://health/sensor";
        String sensorType   = "GPS"; //Search in ontology the value sensor.getId();

        // create an empty Model
        this.model = ModelFactory.createDefaultModel();

        // create the resource
        this.sensor = model.createResource(sensorURI);

        // add the property
        this.sensor.addProperty(VCARD.FN, sensorType); //I am using FN as a temporary way

    }

    @Override
    public void loadKnowledge(String path) throws OntologyFrameworkException {
        Log.d(TAG, ">> TODO: load knowledge String-based");
        //TODO
    }

    @Override
    public void loadKnowledge(Object obg) throws OntologyFrameworkException {
        Log.d(TAG, ">> TODO: load knowledge Object-based");
        //TODO
    }

    @Override
    public void start() {
        Log.d(TAG, ">> TODO: Start ontology framework impl. Jena");
        //TODO
    }

    @Override
    public void stop() {
        Log.d(TAG, ">> TODO: Stop ontology framework impl. Jena");
    }

    @Override
    public Object semanticAnnotation(SensorInterface sensor) {
        this.sensor.addProperty(VCARD.N, sensor.getValue().get(0).toString()); //Lat
        this.sensor.addProperty(VCARD.ADR, sensor.getValue().get(1).toString()); //Long

        Log.d(TAG, ">> Semantic annotation finished successfully!");
        return this.sensor;
    }

    @Override
    public File getRDF(Object object) {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + File.separator + "model.ref");
        try {

            file.createNewFile();
            if(file.exists())
            {
                FileWriter writer = new FileWriter(file);
                model.write(writer);
                writer.close();

                Log.d(TAG, ">> RDF file created successfully!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
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
