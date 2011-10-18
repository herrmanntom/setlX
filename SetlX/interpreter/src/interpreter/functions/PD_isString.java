package interpreter.functions;

import interpreter.types.SetlBoolean;
import interpreter.types.SetlDefinitionParameter;
import interpreter.types.SetlString;
import interpreter.types.Value;

import java.util.List;

public class PD_isString extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isString();

    private PD_isString() {
        super("isString");
        addParameter(new SetlDefinitionParameter("value"));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        if (args.get(0) instanceof SetlString) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }
}
