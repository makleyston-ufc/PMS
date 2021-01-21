package br.ufc.mdcc.cmu.pmslib.cep.statements;

import android.content.Context;

import br.ufc.mdcc.cmu.pmslib.cep.StatementSubscriber;

public class ResourceStatement extends StatementSubscriber {

    public ResourceStatement(Context context) {
        super(context);
    }

    @Override
    public String getStatement() {
        String statement = "select * from Resource()";
        return statement;
    }
}
