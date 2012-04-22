package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;

import java.util.List;

/*
 * ceil(numberValue)       : returns minumum integer which is greater or equal to numberValue
 */

public class PD_ceil extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_ceil();

    private PD_ceil() {
        super("ceil");
        addParameter("numberValue");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).ceil();
    }
}

