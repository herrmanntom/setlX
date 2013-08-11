package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * isInfinite(value) : test if value-type is double and the value is INFINITY or -INFINITY
 */
public class PD_isInfinite extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `isInfinite'. */
    public final static PreDefinedProcedure DEFINITION = new PD_isInfinite();

    private PD_isInfinite() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        return args.get(0).isInfinite();
    }
}

