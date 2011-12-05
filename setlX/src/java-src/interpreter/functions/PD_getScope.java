package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.Value;
import interpreter.utilities.VariableScope;

import java.util.List;

public class PD_getScope extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_getScope();

    private PD_getScope() {
        super("getScope");
        doNotChangeScope();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return VariableScope.getScope().toTerm();
    }
}

