package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.ResetException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// reset()                       : DEBUG: stop execution and return to interactive prompt

public class PD_reset extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_reset();

    private PD_reset() {
        super();
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws ResetException {
        throw new ResetException("reset");
    }
}

