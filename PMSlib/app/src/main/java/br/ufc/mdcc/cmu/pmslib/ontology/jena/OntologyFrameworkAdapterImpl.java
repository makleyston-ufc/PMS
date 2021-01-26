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
import com.hp.hpl.jena.vocabulary.VCARD;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import br.ufc.mdcc.cmu.pmslib.exception.OntologyFrameworkException;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.SensorInterface;
import br.ufc.mdcc.cmu.pmslib.ontology.OntologyFrameworkAdapterInterface;

import static android.content.Context.MODE_PRIVATE;

public final class OntologyFrameworkAdapterImpl extends OntologyFrameworkAdapterInterface {

    private String TAG = getClass().getName();
    private final int MY_PERMISSION_STORAGE_W = 222;
    private final int MY_PERMISSION_STORAGE_R = 333;
    private Resource sensor = null;

    private Model model = null;

    public OntologyFrameworkAdapterImpl(Context context) {
        super(context);

        //this.file = new File(getContext().getFilesDir(), "RDF");

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
        this.sensor.addProperty(VCARD.N, sensor.getValue().get(0).toString()); //Lat
        this.sensor.addProperty(VCARD.ADR, sensor.getValue().get(1).toString()); //Long

        //this.sensor.getProperty(VCARD.FN).getString();
        //Log.d(TAG, ">> value "+this.sensor.getProperty(VCARD.FN).getString());

        Log.d(TAG, ">> Semantic annotation OK!");
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
//            OutputStream fo = new FileOutputStream(file);
//            fo.write(data1);
//            fo.close();
                System.out.println("file created: "+file);
                Log.d(TAG, ">> RDF file OK!");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //byte[] data1={1,1,0,0};
//write the bytes in file


//deleting the file
//        file.delete();
//        System.out.println("file deleted");


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
