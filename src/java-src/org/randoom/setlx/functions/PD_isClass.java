package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * isClass(value) : test if value-type is class
 */
public class PD_isClass extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `isClass'. */
    public final static PreDefinedProcedure DEFINITION = new PD_isClass();

    private PD_isClass() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        return args.get(0).isClass();
    }
}

