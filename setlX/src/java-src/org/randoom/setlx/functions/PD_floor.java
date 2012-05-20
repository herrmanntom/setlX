package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;

import java.util.List;

/*
 * floor(numberValue)      : returns maximum integer which is lower or equal to numberValue
 */

public class PD_floor extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_floor();

    private PD_floor() {
        super("floor");
        addParameter("numberValue");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).floor();
    }
}

