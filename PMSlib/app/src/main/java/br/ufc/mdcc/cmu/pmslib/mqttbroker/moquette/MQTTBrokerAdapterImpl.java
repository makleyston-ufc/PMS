package br.ufc.mdcc.cmu.pmslib.mqttbroker.moquette;

import android.content.Context;
import android.util.Log;
import org.eclipse.moquette.server.Server;
import java.io.IOException;
import br.ufc.mdcc.cmu.pmslib.exception.MQTTBrokerException;
import br.ufc.mdcc.cmu.pmslib.mqttbroker.MQTTBrokerAdapterInterface;

/**
 * Created by makleyston on 14/01/2021
 */

public class MQTTBrokerAdapterImpl extends MQTTBrokerAdapterInterface {

    private final String TAG = getClass().getSimpleName();

    private boolean active = false;
    private Server server = null;

    public MQTTBrokerAdapterImpl(Context context){
        super(context);
    }

    @Override
    public void start() throws MQTTBrokerException {

        final String TAG = getClass().getSimpleName();
        server = new Server();
        try {
            server.startServer();
            Log.d(TAG, "Server Started");
            Log.d(TAG, "Initialized Broker MQTT");
            this.active = true;
        }catch (IOException e) {
            e.printStackTrace();
            throw new MQTTBrokerException();
        }
        catch (Exception e){
            e.printStackTrace();
            throw new MQTTBrokerException();
        }
    }

    @Override
    public void stop() throws MQTTBrokerException {
        if((this.active) && (server != null)) {
            try {
                server.stopServer();
                Log.d(TAG, "Finished Broker MQTT");
                this.active = false;
            } catch (Exception e) {
                e.printStackTrace();
                throw new MQTTBrokerException();
            }
        }
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void requestPermissions() {
        //TODO
    }

}
