package br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makleyston on 14/01/2021.
 */
public abstract class SensorInterface {
    private String id;
    private List<Double> value = new ArrayList<Double>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Double> getValue() {
        return value;
    }

    public void setValue(List<Double> value) {
        this.value = value;
    }
}
