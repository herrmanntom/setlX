package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;

import java.util.List;

/*
 * round(numberValue)      : returns rounded numberValue
 */

public class PD_round extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_round();

    private PD_round() {
        super("round");
        addParameter("numberValue");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).round();
    }
}

