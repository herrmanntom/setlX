package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * char(value) : Converts value into a single ASCII character.
 */
public class PD_char extends PreDefinedProcedure {

    private final static ParameterDef        VALUE      = createParameter("value");

    /** Definition of the PreDefinedProcedure `char'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_char();

    private PD_char() {
        super();
        addParameter(VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) throws SetlException {
        return args.get(VALUE).charConvert(state);
    }
}

