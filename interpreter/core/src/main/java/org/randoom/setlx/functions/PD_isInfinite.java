package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * isInfinite(value) : test if value-type is double and the value is INFINITY or -INFINITY
 */
public class PD_isInfinite extends PreDefinedProcedure {

    private final static ParameterDef        VALUE      = createParameter("value");

    /** Definition of the PreDefinedProcedure `isInfinite'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_isInfinite();

    private PD_isInfinite() {
        super();
        addParameter(VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) {
        return args.get(VALUE).isInfinite();
    }
}

