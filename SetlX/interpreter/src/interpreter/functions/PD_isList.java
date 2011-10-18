package interpreter.functions;

import interpreter.types.SetlBoolean;
import interpreter.types.SetlDefinitionParameter;
import interpreter.types.SetlList;
import interpreter.types.Value;

import java.util.List;

public class PD_isList extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isList();

    private PD_isList() {
        super("isList");
        addParameter(new SetlDefinitionParameter("value"));
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        if (args.get(0) instanceof SetlList) {
            return SetlBoolean.TRUE;
        } else {
            return SetlBoolean.FALSE;
        }
    }
}
