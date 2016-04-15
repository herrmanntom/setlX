package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.AbortException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 *  abort(message) : stops execution and displays given error message(s)
 */
public class PD_abort extends PreDefinedProcedure {

    private final static ParameterDefinition MESSAGE    = createListParameter("message");

    /** Definition of the PreDefinedProcedure `abort'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_abort();

    private PD_abort() {
        super();
        addParameter(MESSAGE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws AbortException {
        final StringBuilder message = new StringBuilder();
        message.append("abort: ");
        for (final Value arg : (SetlList) args.get(MESSAGE)) {
            arg.appendUnquotedString(state, message, 0);
        }
        throw new AbortException(message.toString());
    }
}

