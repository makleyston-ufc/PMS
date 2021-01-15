package br.ufc.mdcc.cmu.pmslib.iotmiddleware.sensors;

/**
 * Created by makleyston on 14/01/2021.
 */
public abstract class SensorInterface {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
