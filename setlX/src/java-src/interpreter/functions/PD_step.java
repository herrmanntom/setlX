package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.Value;
import interpreter.utilities.DebugPrompt;

import java.util.List;

// step()                        : DEBUG: execute next statement

public class PD_step extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_step();

    private PD_step() {
        super("step");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        DebugPrompt.stopPrompt();
        return Om.OM.hide();
    }
}

