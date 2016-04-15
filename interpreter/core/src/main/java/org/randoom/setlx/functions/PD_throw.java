package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.ThrownInSetlXException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * throw(value) : stops execution and throws value to be catched by try-catch block
 */
public class PD_throw extends PreDefinedProcedure {

    private final static ParameterDefinition VALUE      = createParameter("value");

    /** Definition of the PreDefinedProcedure `throw'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_throw();

    private PD_throw() {
        super();
        addParameter(VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws ThrownInSetlXException {
        throw new ThrownInSetlXException(state, args.get(VALUE));
    }
}

