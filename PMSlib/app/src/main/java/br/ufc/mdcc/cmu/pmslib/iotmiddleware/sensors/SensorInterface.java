package br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makleyston on 14/01/2021.
 */
public abstract class SensorInterface {
    private String id;
    private String type;
    private String vocabulary;
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

    public String getType() {
        return type;
    }

    public String getFullType() {
        return vocabulary + type;
    }

    public void setType(String type) {
        this.type = type;
        this.vocabulary = "http://www.w3.org/2003/01/geo/wgs84_pos/";
    }

    public void setType(String vocabulary, String type) {
        this.type = type;
        this.vocabulary = vocabulary;
    }
}
