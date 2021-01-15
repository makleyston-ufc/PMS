package br.ufc.mdcc.cmu.pmslib;

import android.content.Context;

import br.ufc.mdcc.cmu.pmslib.exception.PMSException;

public interface PMSInterface {

    public void start() throws PMSException;
    public void stop() throws PMSException;

}
