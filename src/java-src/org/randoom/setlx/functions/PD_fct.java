package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * fct(term) : Get functional char of a term.
 */
public class PD_fct extends PreDefinedProcedure {

    private final static ParameterDef        TERM       = createParameter("term");

    /** Definition of the PreDefinedProcedure `fct'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_fct();

    private PD_fct() {
        super();
        addParameter(TERM);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) throws SetlException {
        return args.get(TERM).functionalCharacter(state);
    }
}

