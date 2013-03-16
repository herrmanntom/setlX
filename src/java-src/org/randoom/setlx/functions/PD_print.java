package org.randoom.setlx.functions;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// print(value, ...)             : prints string representation of provided value into stdout

public class PD_print extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_print();

    protected PD_print() {
        super();
        addParameter("value");
        enableUnlimitedParameters();
        allowFewerParameters();
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        final StringBuilder out = new StringBuilder();
        for (final Value arg : args) {
            arg.appendUnquotedString(state, out, 0);
            print(state, out.toString());
            out.setLength( 0 );
        }
        printEndl(state);
        return Om.OM;
    }

    protected void print(final State state, final String txt) {
        state.outWrite(txt);
    }

    protected void printEndl(final State state) {
        state.outWriteLn();
    }
}

