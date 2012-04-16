package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.Value;
import interpreter.utilities.DebugPrompt;
import interpreter.utilities.Environment;

import java.util.List;

// resume()                      : DEBUG: resume normal execution

public class PD_resume extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_resume();

    private PD_resume() {
        super("resume");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        DebugPrompt.stopPrompt();
        Environment.setDebugModeActive(false);
        return Om.OM.hide();
    }
}

