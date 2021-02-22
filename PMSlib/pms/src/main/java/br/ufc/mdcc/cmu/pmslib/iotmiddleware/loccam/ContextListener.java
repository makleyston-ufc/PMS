package br.ufc.mdcc.cmu.pmslib.iotmiddleware.loccam;

public interface ContextListener {

    void onContextReady(String data);
    String getContextKey();
}
