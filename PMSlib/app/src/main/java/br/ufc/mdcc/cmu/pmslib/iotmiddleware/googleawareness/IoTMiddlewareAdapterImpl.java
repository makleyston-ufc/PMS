package br.ufc.mdcc.cmu.pmslib.iotmiddleware.googleawareness;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResponse;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import br.ufc.mdcc.cmu.pmslib.exception.IoTMiddlewareException;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.IoTMiddlewareAdapterInterface;
import br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors.SensorInterface;

/**
 * Created by makleyston on 14/01/2021.
 */

public class IoTMiddlewareAdapterImpl extends IoTMiddlewareAdapterInterface {

    private final String TAG = getClass().getSimpleName();
    private final int MY_PERMISSION_LOCATION = 123;
    private boolean active = false;

    public IoTMiddlewareAdapterImpl(Context context){
        super(context);
    }

    @Override
    public void start() throws IoTMiddlewareException {
        try {
            startSnapshot();
            Log.d(TAG, ">> IoT middleware 'Google Awareness' was started successfully!");
            this.active = true;
        }catch (Exception e){
            e.getMessage();
            e.printStackTrace();
            throw new IoTMiddlewareException();
        }

    }

    @Override
    public void stop() throws IoTMiddlewareException {
        try{
            Log.d(TAG, ">> IoT middleware 'Google Awareness' was stopped successfully!");
            stopSnapshot();
            this.active = false;
        }catch (Exception e){
            e.getMessage();
            e.printStackTrace();
            throw new IoTMiddlewareException();
        }
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void requestPermissions() {
        /*Request permission*/
        if (ContextCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    (Activity) getContext(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_LOCATION
            );
            return;
        }
    }

    private void stopSnapshot() {
        //TODO
    }

    private void startSnapshot(){
        Log.i(TAG, ">> Snapshot activated successfully - Google Awareness!");
        Awareness.getSnapshotClient(getContext()).getLocation()
                .addOnSuccessListener(new OnSuccessListener<LocationResponse>() {
                    @Override
                    public void onSuccess(LocationResponse locationResponse) {
                        try {
                            SensorInterface sensor = new SensorInterface() {};

                            List<Double> values = new ArrayList<>();
                            values.add(locationResponse.getLocation().getLatitude());
                            values.add(locationResponse.getLocation().getLongitude());
                            sensor.setValue(values);
                            sensor.setId("GPS");

                            getIoTMiddlewareListener().onReceiveData(sensor);
                        } catch (IoTMiddlewareException e) {
                            Log.e(TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
    }


}
