package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/*
 * ceil(numberValue)       : returns minumum integer which is greater or equal to numberValue
 */

public class PD_ceil extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_ceil();

    private PD_ceil() {
        super();
        addParameter("numberValue");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).ceil(state);
    }
}

