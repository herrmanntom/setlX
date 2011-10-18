package interpreter.functions;

import interpreter.exceptions.AbortException;
import interpreter.types.SetlDefinitionParameter;
import interpreter.types.SetlString;
import interpreter.types.Value;

import java.util.List;

public class PD_abort extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_abort();

    private PD_abort() {
        super("abort");
        addParameter(new SetlDefinitionParameter("firstMessage"));
        enableUnlimitedParameters();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws AbortException {
        String msg = "";
        for (Value arg : args) {
            // output Strings without double-quotes and escape characters
            if (arg instanceof SetlString) {
                msg += ((SetlString) arg).toStringForPrint();
            } else {
                msg += arg.toString();
            }
        }
        throw new AbortException("abort: " + msg);
    }
}
