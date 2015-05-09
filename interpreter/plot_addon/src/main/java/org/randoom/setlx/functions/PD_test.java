package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

public class PD_test extends PreDefinedProcedure {


    public final static PreDefinedProcedure
            DEFINITION = new PD_test();

    private PD_test() {
        super();
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        return new SetlString("String");
    }
}
