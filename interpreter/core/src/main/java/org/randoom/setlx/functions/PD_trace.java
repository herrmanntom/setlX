package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * trace(toggle) : Configures output of all assignments.
 */
public class PD_trace extends PreDefinedProcedure {

    private final static ParameterDefinition TOGGLE     = createParameter("toggle");

    /** Definition of the PreDefinedProcedure `trace'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_trace();

    private PD_trace() {
        super();
        addParameter(TOGGLE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws IncompatibleTypeException {
        final Value toggle = args.get(TOGGLE);
        if ( ! (toggle instanceof SetlBoolean)) {
            throw new IncompatibleTypeException(
                "Toggle-argument '" + toggle.toString(state) + "' is not a Boolean value."
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

