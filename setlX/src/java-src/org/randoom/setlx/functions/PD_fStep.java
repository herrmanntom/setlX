package org.randoom.setlx.functions;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.DebugPrompt;
import org.randoom.setlx.utilities.Environment;

import java.util.List;

// fStep()                       : DEBUG: continue execution of next function until it returns

public class PD_fStep extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_fStep();

    private PD_fStep() {
        super("fStep");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        Environment.setDebugStepThroughFunction(true);
        DebugPrompt.stopPrompt();
        return Om.OM.hide();
    }
}

