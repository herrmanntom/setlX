package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;

import java.util.List;

// rnd(numberOrCollectionValue)  : If argument is a number, returns a random number
//                                 between 0 and the argument (inclusive).
//                                 Otherwise if the argument is a collectionValue,
//                                 a randomly selected member will be returned.

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

