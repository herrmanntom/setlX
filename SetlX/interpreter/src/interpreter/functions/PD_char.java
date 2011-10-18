package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlDefinitionParameter;
import interpreter.types.Value;

import java.util.List;

public class PD_char extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_char();

    private PD_char() {
        super("char");
        addParameter(new SetlDefinitionParameter("value"));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).charConvert();
    }
}
