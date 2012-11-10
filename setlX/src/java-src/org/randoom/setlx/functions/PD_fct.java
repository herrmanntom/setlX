package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// fct(term)               : get functional char of a term

public class PD_fct extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_fct();

    private PD_fct() {
        super("fct");
        addParameter("term");
    }

    public SetlString execute(final State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).functionalCharacter();
    }
}

