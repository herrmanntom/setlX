package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.Value;

import java.util.List;

// abs(value)              : returns the absolute (e.g. positive) value of the parameter

public class PD_abs extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_abs();

    private PD_abs() {
        super("abs");
        addParameter("value");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).absoluteValue();
    }
}

