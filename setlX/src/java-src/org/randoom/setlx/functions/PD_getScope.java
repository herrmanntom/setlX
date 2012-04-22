package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.VariableScope;

import java.util.List;

// getScope()              : get a term representing all variables set in current scope

public class PD_getScope extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_getScope();

    private PD_getScope() {
        super("getScope");
        doNotChangeScope();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return VariableScope.getScope().toTerm();
    }
}

