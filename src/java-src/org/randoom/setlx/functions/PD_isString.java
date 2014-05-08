package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * isString(value) : Test if value-type is string.
 */
public class PD_isString extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `isString'. */
    public final static PreDefinedProcedure DEFINITION = new PD_isString();

    private PD_isString() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        return args.get(0).isString();
    }
}

