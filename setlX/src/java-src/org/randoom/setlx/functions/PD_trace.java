package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;

import java.util.List;

// trace(toggle)             : configures output of all assignments

public class PD_trace extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_trace();

    private PD_trace() {
        super("trace");
        addParameter("toggle");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws IncompatibleTypeException {
        Value   toggle  = args.get(0);
        if ( ! (toggle instanceof SetlBoolean)) {
            throw new IncompatibleTypeException(
                "toggle-argument '" + toggle + "' is not a Boolean value."
            );
        }

        if (toggle == SetlBoolean.TRUE) {
            Environment.setTraceAssignments(true);
        } else /* if (toggle == SetlBoolean.FALSE) */ {
            Environment.setTraceAssignments(false);
        }

        // everything seems fine
        return toggle;
    }
}

