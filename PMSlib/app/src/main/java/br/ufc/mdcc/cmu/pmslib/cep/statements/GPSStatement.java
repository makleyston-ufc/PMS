package br.ufc.mdcc.cmu.pmslib.cep.statements;

import android.content.Context;
import android.util.Log;

import br.ufc.mdcc.cmu.pmslib.cep.StatementSubscriber;

public class GPSStatement extends StatementSubscriber {

    private final String TAG = getClass().getSimpleName();
    public GPSStatement(Context context) {
        super(context);
        addDomain("/teste");
    }

    @Override
    public String getStatement() {
        String stm = "select * from GPS(lat='-3.7710668')";
        return stm;
    }

    @Override
    public void update(Object eventMap) {
        super.update(eventMap);
    }
}
