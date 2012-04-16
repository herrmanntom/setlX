package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.Value;
import interpreter.utilities.DebugPrompt;
import interpreter.utilities.Environment;

import java.util.List;

// finish()                      : DEBUG: continue execution of current function until it returns

public class PD_finish extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_finish();

    private PD_finish() {
        super("finish");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        Environment.setDebugFinishFunction(true);
        Environment.setDebugModeActive(false);
        DebugPrompt.stopPrompt();
        return Om.OM.hide();
    }
}

