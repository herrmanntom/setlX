package interpreter.functions;

import interpreter.types.SetlBoolean;
import interpreter.types.SetlDefinitionParameter;
import interpreter.types.SetlReal;
import interpreter.types.Value;

import java.util.List;

public class PD_isReal extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isReal();

    private PD_isReal() {
        super("isReal");
        addParameter(new SetlDefinitionParameter("value"));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        if (args.get(0) instanceof SetlReal) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }
}
