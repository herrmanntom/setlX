package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * arb(collectionValue) : Selects an arbitrary member from `collectionValue'.
 *                        Note that 'arb' is deterministic, while rnd is not.
 */
public class PD_arb extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `arb'. */
    public final static PreDefinedProcedure DEFINITION = new PD_arb();

    private PD_arb() {
        super();
        addParameter("collectionValue");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).arbitraryMember(state);
    }
}

