package interpreter.functions;

import interpreter.exceptions.AbortException;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.ParameterDef;

import java.util.List;

public class PD_abort extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_abort();

    private PD_abort() {
        super("abort");
        addParameter(new ParameterDef("firstMessage"));
        enableUnlimitedParameters();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws AbortException {
        String msg = "";
        for (Value arg : args) {
            // output Strings without double-quotes and escape characters
            msg += arg.toStringForPrint();
        }
        throw new AbortException("abort: " + msg);
    }
}

