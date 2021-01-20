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

        /*Request permission*/
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_LOCATION
            );
            return;
        }

    }

    @Override
    public void start() throws IoTMiddlewareException {
        Log.d(TAG, "startIoTMiddleware");
        startSnapshat();
        this.active = true;
    }

    @Override
    public void stop() throws IoTMiddlewareException {
        Log.d(TAG, "stopIoTMiddleware");
        stopSnapshat();
        this.active = false;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    private void stopSnapshat() {
        //TODO
    }

    private void startSnapshat(){
        Log.i(TAG, "Actived Snapshot - Google Awareness!");
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
