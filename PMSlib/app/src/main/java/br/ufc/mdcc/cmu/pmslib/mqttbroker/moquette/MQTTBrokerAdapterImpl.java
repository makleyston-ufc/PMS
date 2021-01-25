package br.ufc.mdcc.cmu.pmslib.mqttbroker.moquette;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import br.ufc.mdcc.cmu.pmslib.exception.MQTTBrokerException;
import br.ufc.mdcc.cmu.pmslib.mqttbroker.MQTTBrokerAdapterInterface;
import io.moquette.BrokerConstants;
import io.moquette.server.Server;
import io.moquette.server.config.MemoryConfig;

/**
 * Created by makleyston on 14/01/2021
 */

public class MQTTBrokerAdapterImpl extends MQTTBrokerAdapterInterface {

    private final String TAG = getClass().getSimpleName();

    private boolean active = false;
    private Server server = null;

    @Override
    public void start() throws MQTTBrokerException {

        final String TAG = getClass().getSimpleName();

        server = new Server();
        try {
            MemoryConfig memoryConfig = new MemoryConfig(new Properties());
            memoryConfig.setProperty
                    (BrokerConstants.PERSISTENT_STORE_PROPERTY_NAME,
                            Environment.getExternalStorageDirectory().getAbsolutePath()+
                                    File.separator + BrokerConstants.DEFAULT_MOQUETTE_STORE_MAP_DB_FILENAME);
            server.startServer(memoryConfig);
            // server.startServer();//is not working due to DEFAULT_MOQUETTE_STORE_MAP_DB_FILENAME;
            Log.d(TAG,"Server Started");
            Log.d(TAG, "Initialized Broker MQTT");
            this.active = true;
        }
        catch (IOException e) {
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

}
