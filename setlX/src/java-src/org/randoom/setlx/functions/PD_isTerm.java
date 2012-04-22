package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;

import java.util.List;

// isTerm(value)           : test if value-type is term

public class PD_isTerm extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isTerm();

    private PD_isTerm() {
        super("isTerm");
        addParameter("value");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return args.get(0).isTerm();
    }
}

