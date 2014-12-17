package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * pow(set) : Computes the power-set.
 */
public class PD_pow extends PreDefinedProcedure {

    private final static ParameterDef        SET        = createParameter("set");

    /** Definition of the PreDefinedProcedure `pow'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_pow();

    private PD_pow() {
        super();
        addParameter(SET);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) throws SetlException {
        return args.get(SET).powerSet(state);
    }
}

