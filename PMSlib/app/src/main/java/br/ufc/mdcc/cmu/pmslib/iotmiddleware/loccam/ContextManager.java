package br.ufc.mdcc.cmu.pmslib.iotmiddleware.loccam;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.ufc.great.loccamlib.LoccamListener;
import br.ufc.great.loccamlib.LoccamManager;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.interfaces.IClientReaction;
import br.ufc.great.syssu.base.interfaces.ISysSUService;


public class ContextManager implements LoccamListener {

    private static ContextManager instance;
    private Context context;
    private ArrayList<ContextListener> listeners;
    private LoccamManager loccamManager;
    private boolean alreadyConnected = false;
    private final String TAG = getClass().getSimpleName();

    private ContextManager(){
        listeners = new ArrayList<>();
    }

    public static ContextManager getInstance(){
        if(instance == null)
            instance = new ContextManager();
        return instance;
    }


    public void connect(Context context, String appId){
        this.context = context;

        loccamManager = new LoccamManager(context, appId);
        loccamManager.connect(this);
    }

    public void disconnect(){
        if(loccamManager != null){
            loccamManager.disconnect();
        }
    }

    public void registerListener(final ContextListener listener){
        listeners.add(listener);

    }

    public void unregisterListener(ContextListener listener){
        if(listeners.contains(listener)){
            listeners.remove(listener);
            loccamManager.finish(listener.getContextKey());
        }
    }

    @Override
    public void onServiceConnected(ISysSUService iSysSUService) {

        Log.d(TAG,"Loccam was connected successfully!");

        for(final ContextListener listener:listeners) {
            loccamManager.init(listener.getContextKey());
            loccamManager.getAsync(new IClientReaction.Stub() {
                @Override
                public void react(Tuple tuple) throws RemoteException {
                    listener.onContextReady(tuple.getField(2).getValue().toString());
                }
            }, "put", ContextKeys.CKs.get(0), null);
        }
    }

    @Override
    public void onServiceDisconnected() {
        Log.d(TAG,"Loccam was disconnected successfully!");
    }

    @Override
    public void onLoccamException(Exception e) {
        Log.d(TAG,e.getMessage());
    }

    public void initLoccam(List<String> listaSacs){
        for (String string : listaSacs){
            loccamManager.init(string);
        }
    }
}
