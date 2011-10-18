package interpreter.functions;

import interpreter.types.SetlBoolean;
import interpreter.types.SetlDefinitionParameter;
import interpreter.types.SetlInt;
import interpreter.types.Value;

import java.util.List;

public class PD_isInteger extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isInteger();

    private PD_isInteger() {
        super("isInteger");
        addParameter(new SetlDefinitionParameter("value"));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        if (args.get(0) instanceof SetlInt) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }
}
