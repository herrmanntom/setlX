package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * arb(collectionValue) : Selects an arbitrary member from `collectionValue'.
 *                        Note that 'arb' is deterministic, while rnd is not.
 */
public class PD_arb extends PreDefinedProcedure {

    private final static ParameterDefinition COLLECTION_VALUE = createParameter("collectionValue");

    /** Definition of the PreDefinedProcedure `arb'. */
    public  final static PreDefinedProcedure DEFINITION       = new PD_arb();

    private PD_arb() {
        super();
        addParameter(COLLECTION_VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        return args.get(COLLECTION_VALUE).arbitraryMember(state);
    }
}

