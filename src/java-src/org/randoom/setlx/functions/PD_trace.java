package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// trace(toggle)                 : configures output of all assignments

public class PD_trace extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_trace();

    private PD_trace() {
        super();
        addParameter("toggle");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        final Value   toggle  = args.get(0);
        if ( ! (toggle instanceof SetlBoolean)) {
            throw new IncompatibleTypeException(
                "Toggle-argument '" + toggle + "' is not a Boolean value."
            );
        }

        if (toggle == SetlBoolean.TRUE) {
            state.setTraceAssignments(true);
        } else /* if (toggle == SetlBoolean.FALSE) */ {
            state.setTraceAssignments(false);
        }

        // everything seems fine
        return toggle;
    }
}

