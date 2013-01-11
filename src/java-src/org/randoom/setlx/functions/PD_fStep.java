package org.randoom.setlx.functions;

import org.randoom.setlx.types.SetlError;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.DebugPrompt;
import org.randoom.setlx.utilities.State;

import java.util.List;

// fStep()                       : DEBUG: continue execution of next function until it returns

public class PD_fStep extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_fStep();

    private PD_fStep() {
        super("fStep");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        state.setDebugStepThroughFunction(true);
        DebugPrompt.stopPrompt();
        return new SetlError("fStep");
    }
}

