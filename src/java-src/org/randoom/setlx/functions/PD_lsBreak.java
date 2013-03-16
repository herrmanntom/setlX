package org.randoom.setlx.functions;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// lsBreak()                     : DEBUG: list breakpoints

public class PD_lsBreak extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_lsBreak();

    private PD_lsBreak() {
        super();
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        final String head    = "Debugger Breakpoints:\n";
        String       message = head;

        for (final String point : state.getAllBreakpoints()) {
            message += "  " + point + "\n";
        }

        if (message.equals(head)) {
            message += " no breakpoints set\n";
        }

        state.errWriteLn(message);

        return Om.OM;
    }
}

