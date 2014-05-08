package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * getTerm(value) : convert a value into a term
 */
public class PD_getTerm extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `getTerm'. */
    public final static PreDefinedProcedure DEFINITION = new PD_getTerm();

    private PD_getTerm() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        return args.get(0).toTerm(state);
    }
}

