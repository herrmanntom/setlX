package interpreter.functions;

import interpreter.exceptions.AbortException;
import interpreter.types.SetlString;
import interpreter.types.Value;

import java.util.List;

// abort(message)          : stops execution and displays given error message(s)

public class PD_abort extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_abort();

    private PD_abort() {
        super("abort");
        addParameter("message");
        enableUnlimitedParameters();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws AbortException {
        String msg = "";
        for (Value arg : args) {
            String text = arg.toString();
            // Strip out double qoutes when printing strings
            int length = text.length();
            if (length >= 2 && text.charAt(0) == '"' && text.charAt(length - 1) == '"') {
                text = text.substring(1, length - 1);
            }
            msg += text;
        }
        throw new AbortException("abort: " + msg);
    }
}

