package interpreter.functions;

import interpreter.types.SetlBoolean;
import interpreter.types.SetlReal;
import interpreter.types.Value;
import interpreter.utilities.ParameterDef;

import java.util.List;

public class PD_isReal extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isReal();

    private PD_isReal() {
        super("isReal");
        addParameter(new ParameterDef("value"));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        if (args.get(0) instanceof SetlReal) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }
}

