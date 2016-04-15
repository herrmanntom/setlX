package org.randoom.setlx.functions;

import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * isNumber(value) : Test if value-type is a rational or double.
 */
public class PD_isNumber extends PreDefinedProcedure {

    private final static ParameterDefinition VALUE      = createParameter("value");

    /** Definition of the PreDefinedProcedure `isNumber'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_isNumber();

    private PD_isNumber() {
        super();
        addParameter(VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) {
        final Value arg = args.get(VALUE);
        if (arg.isRational() == SetlBoolean.TRUE || arg.isDouble() == SetlBoolean.TRUE) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }
}

