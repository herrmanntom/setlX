package org.randoom.setlx.functions;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.DebugPrompt;
import org.randoom.setlx.utilities.Environment;

import java.util.List;

// uStep()                       : DEBUG: halt before evaluating next expression

public class PD_uStep extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_uStep();

    private PD_uStep() {
        super("uStep");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        Environment.setDebugStepNextExpr(true);
        DebugPrompt.stopPrompt();
        return Om.OM.hide();
    }
}

