package org.randoom.setlx.functions;

import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// isNumber(value)         : test if value-type is a rational or double

public class PD_isNumber extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_isNumber();

    private PD_isNumber() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        final Value arg = args.get(0);
        if (arg.isRational() == SetlBoolean.TRUE || arg.isDouble() == SetlBoolean.TRUE) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }
}

