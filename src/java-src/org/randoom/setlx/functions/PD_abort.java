package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.AbortException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// abort(message)          : stops execution and displays given error message(s)

public class PD_abort extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_abort();

    private PD_abort() {
        super();
        addParameter("message");
        enableUnlimitedParameters();
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws AbortException {
        String msg = "";
        for (final Value arg : args) {
            msg += arg.getUnquotedString();
        }
        throw new AbortException("abort: " + msg);
    }
}

