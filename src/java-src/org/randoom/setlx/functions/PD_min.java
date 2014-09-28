package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * min(collectionValue) : Select minimum member from collection value.
 */
public class PD_min extends PreDefinedProcedure {

    private final static ParameterDef        COLLECTION_VALUE = createParameter("collectionValue");

    /** Definition of the PreDefinedProcedure `min'. */
    public  final static PreDefinedProcedure DEFINITION       = new PD_min();

    private PD_min() {
        super();
        addParameter(COLLECTION_VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) throws SetlException {
        return args.get(COLLECTION_VALUE).minimumMember(state);
    }
}

