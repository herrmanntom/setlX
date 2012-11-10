package org.randoom.setlx.functions;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.DebugPrompt;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.State;

import java.util.List;

// resume()                      : DEBUG: resume normal execution

public class PD_resume extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_resume();

    private PD_resume() {
        super("resume");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) {
        DebugPrompt.stopPrompt();
        Environment.setDebugModeActive(false);
        return Om.OM.hide();
    }
}

