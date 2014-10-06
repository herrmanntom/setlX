package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * la_isVector(value) : Test if value-type is vector.
 */
public class PD_la_isVector extends PreDefinedProcedure {

    private final static ParameterDef        VALUE      = createParameter("value");

    /** Definition of the PreDefinedProcedure `la_isVector'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_la_isVector();

    private PD_la_isVector() {
        super();
        addParameter(VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) {
        return args.get(VALUE).isVector();
    }
}

