package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * round(numberValue) : Returns rounded numberValue.
 */
public class PD_round extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `round'. */
    public final static PreDefinedProcedure DEFINITION = new PD_round();

    private PD_round() {
        super();
        addParameter("numberValue");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).round(state);
    }
}

