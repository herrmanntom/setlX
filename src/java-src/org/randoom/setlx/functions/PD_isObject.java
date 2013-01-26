package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// isObject(Value)         : test if value-type is object

public class PD_isObject extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isObject();

    private PD_isObject() {
        super("isObject");
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        return args.get(0).isObject();
    }
}

