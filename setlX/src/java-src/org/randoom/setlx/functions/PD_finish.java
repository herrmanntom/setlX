package org.randoom.setlx.functions;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.DebugPrompt;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.State;

import java.util.List;

// finish()                      : DEBUG: continue execution of current function until it returns

public class PD_finish extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_finish();

    private PD_finish() {
        super("finish");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) {
        Environment.setDebugFinishFunction(true);
        Environment.setDebugModeActive(false);
        DebugPrompt.stopPrompt();
        return Om.OM.hide();
    }
}

