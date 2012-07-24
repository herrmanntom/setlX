package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;

import java.util.List;

// rnd(collectionValue)    : randomly selects a member from `collectionValue'; different from arb, as arb is deterministic and rnd is not

public class PD_rnd extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_rnd();

    private PD_rnd() {
        super("rnd");
        addParameter("collectionValue");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).random();
    }
}

