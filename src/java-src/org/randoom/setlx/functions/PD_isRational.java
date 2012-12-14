package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// isRational(value)       : test if value-type is rational

public class PD_isRational extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isRational();

    private PD_isRational() {
        super("isRational");
        addParameter("value");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) {
        return args.get(0).isRational();
    }
}

