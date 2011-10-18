package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlDefinitionParameter;
import interpreter.types.Value;

import java.util.List;

public class PD_abs extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_abs();

    private PD_abs() {
        super("abs");
        addParameter(new SetlDefinitionParameter("value"));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).absoluteValue();
    }
}
