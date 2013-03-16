package org.randoom.setlx.functions;

import org.randoom.setlx.types.SetlError;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.DebugPrompt;
import org.randoom.setlx.utilities.State;

import java.util.List;

// finishLoop()                  : DEBUG: continue execution of current loop until it finishes

public class PD_finishLoop extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_finishLoop();

    private PD_finishLoop() {
        super();
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        state.setDebugFinishLoop(true);
        state.setDebugModeActive(false);
        DebugPrompt.stopPrompt();
        return new SetlError("finishLoop");
    }
}

