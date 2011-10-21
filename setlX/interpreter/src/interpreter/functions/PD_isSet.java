package interpreter.functions;

import interpreter.types.SetlBoolean;
import interpreter.types.SetlSet;
import interpreter.types.Value;
import interpreter.utilities.ParameterDef;

import java.util.List;

public class PD_isSet extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isSet();

    private PD_isSet() {
        super("isSet");
        addParameter(new ParameterDef("value"));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        if (args.get(0) instanceof SetlSet) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }
}

