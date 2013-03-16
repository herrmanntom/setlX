package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/*
 * floor(numberValue)      : returns maximum integer which is lower or equal to numberValue
 */

public class PD_floor extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_floor();

    private PD_floor() {
        super();
        addParameter("numberValue");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).floor(state);
    }
}

