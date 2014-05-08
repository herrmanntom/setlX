package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * isBoolean(value) : Test if value-type is boolean.
 */
public class PD_isBoolean extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `isBoolean'. */
    public final static PreDefinedProcedure DEFINITION = new PD_isBoolean();

    private PD_isBoolean() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        return args.get(0).isBoolean();
    }
}

