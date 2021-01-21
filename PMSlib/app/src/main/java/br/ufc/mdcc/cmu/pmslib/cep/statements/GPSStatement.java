package br.ufc.mdcc.cmu.pmslib.cep.statements;

import android.content.Context;

import br.ufc.mdcc.cmu.pmslib.cep.StatementSubscriber;

public class GPSStatement extends StatementSubscriber {

    public GPSStatement(Context context) {
        super(context);
    }

    @Override
    public String getStatement() {
        String stm = "select * from Resource(id = 'GPS' and lat='30.012' and lng='140.234')";
        return stm;
    }
}
