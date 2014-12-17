package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * shuffle(collectionValue) : returns a randomly shuffled version of the collectionValue.
 */
public class PD_shuffle extends PreDefinedProcedure {

    private final static ParameterDef        COLLECTION_VALUE = createParameter("collectionValue");

    /** Definition of the PreDefinedProcedure `shuffle'. */
    public  final static PreDefinedProcedure DEFINITION       = new PD_shuffle();

    private PD_shuffle() {
        super();
        addParameter(COLLECTION_VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) throws SetlException {
        return args.get(COLLECTION_VALUE).shuffle(state);
    }

}

