package interpreter.functions;

import interpreter.exceptions.AbortException;
import interpreter.types.SetlString;
import interpreter.types.Value;

import java.util.List;

public class PD_abort extends PreDefinedFunction {
    public final static PD_abort DEFINITION = new PD_abort();

    private PD_abort() {
        super("abort");
    }

    public Value call(List<Value> args) throws AbortException {
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

    public boolean writeVars() {
        return false;
    }
}
