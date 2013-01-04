package org.randoom.setlx.functions;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.DebugPrompt;
import org.randoom.setlx.utilities.State;

import java.util.List;

// resume()                      : DEBUG: resume normal execution

public class PD_resume extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_resume();

    private PD_resume() {
        super("resume");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        DebugPrompt.stopPrompt();
        state.setDebugModeActive(false);
        return Om.OM.hide();
    }
}

