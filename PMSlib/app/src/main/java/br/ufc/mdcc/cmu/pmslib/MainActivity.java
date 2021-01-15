package br.ufc.mdcc.cmu.pmslib;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import br.ufc.mdcc.cmu.pmslib.exception.PMSException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PMS pms = PMS.getInstance(this);
        try {
            pms.start();
        } catch (PMSException e) {
            e.printStackTrace();
        }
    }
}
