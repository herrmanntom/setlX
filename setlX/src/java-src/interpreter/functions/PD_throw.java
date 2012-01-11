package interpreter.functions;

import interpreter.exceptions.ThrownInSetlXException;
import interpreter.types.Value;

import java.util.List;

// throw(value)            : stops execution and throws value to be catched by try-catch block

public class PD_throw extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_throw();

    private PD_throw() {
        super("throw");
        addParameter("value");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws ThrownInSetlXException {
        throw new ThrownInSetlXException(args.get(0));
    }
}

