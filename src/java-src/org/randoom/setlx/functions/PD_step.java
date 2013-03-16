package org.randoom.setlx.functions;

import org.randoom.setlx.types.SetlError;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.DebugPrompt;
import org.randoom.setlx.utilities.State;

import java.util.List;

// step()                        : DEBUG: execute next statement

public class PD_step extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_step();

    private PD_step() {
        super();
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        DebugPrompt.stopPrompt();
        return new SetlError("step");
    }
}

