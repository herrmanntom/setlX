package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * fct(term) : Get functional char of a term.
 */
public class PD_fct extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `fct'. */
    public final static PreDefinedProcedure DEFINITION = new PD_fct();

    private PD_fct() {
        super();
        addParameter("term");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).functionalCharacter(state);
    }
}

