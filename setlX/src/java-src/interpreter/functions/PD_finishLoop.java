package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.Value;
import interpreter.utilities.DebugPrompt;
import interpreter.utilities.Environment;

import java.util.List;

// finishLoop()                  : DEBUG: continue execution of current loop until it finishes

public class PD_finishLoop extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_finishLoop();

    private PD_finishLoop() {
        super("finishLoop");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        Environment.setDebugFinishLoop(true);
        Environment.setDebugModeActive(false);
        DebugPrompt.stopPrompt();
        return Om.OM.hide();
    }
}

