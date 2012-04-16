package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.Value;
import interpreter.utilities.DebugPrompt;
import interpreter.utilities.Environment;

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

