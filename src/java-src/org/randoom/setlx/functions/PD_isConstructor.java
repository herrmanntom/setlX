package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// isConstructor(value)    : test if value-type is constructor

public class PD_isConstructor extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isConstructor();

    private PD_isConstructor() {
        super("isConstructor");
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        return args.get(0).isConstructor();
    }
}

