package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * max(collectionValue) : Select maximum member from collection value.
 */
public class PD_max extends PreDefinedProcedure {

    private final static ParameterDef        COLLECTION_VALUE = createParameter("collectionValue");

    /** Definition of the PreDefinedProcedure `max'. */
    public  final static PreDefinedProcedure DEFINITION       = new PD_max();

    private PD_max() {
        super();
        addParameter(COLLECTION_VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) throws SetlException {
        return args.get(COLLECTION_VALUE).maximumMember(state);
    }
}

