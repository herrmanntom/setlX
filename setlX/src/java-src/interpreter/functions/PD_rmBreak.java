package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.types.SetlBoolean;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.Environment;

import java.util.List;

// rmBreak("id")                 : DEBUG: removes breakpoint in function bound to "id"

public class PD_rmBreak extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_rmBreak();

    private PD_rmBreak() {
        super("rmBreak");
        addParameter("id");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws IncompatibleTypeException {
        Value   id  = args.get(0);
        if ( ! (id instanceof SetlString)) {
            throw new IncompatibleTypeException("id-argument '" + id + "' is not a string.");
        }

        if (id.equals(new SetlString("*"))) {
            Environment.removeAllBreakpoints();
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.get(Environment.removeBreakpoint(((SetlString) id).getUnquotedString()));
        }
    }
}

