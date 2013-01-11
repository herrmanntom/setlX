package org.randoom.setlx.functions;

import org.randoom.setlx.types.SetlError;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.DebugPrompt;
import org.randoom.setlx.utilities.State;

import java.util.List;

// finish()                      : DEBUG: continue execution of current function until it returns

public class PD_finish extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_finish();

    private PD_finish() {
        super("finish");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        state.setDebugFinishFunction(true);
        state.setDebugModeActive(false);
        DebugPrompt.stopPrompt();
        return new SetlError("finish");
    }
}

