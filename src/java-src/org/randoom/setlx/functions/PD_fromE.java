package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef.ParameterType;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * fromE(rw collectionValue)     : selects and removes the last member from `collectionValue'
 */
public class PD_fromE extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `fromE'. */
    public final static PreDefinedProcedure DEFINITION = new PD_fromE();

    private PD_fromE() {
        super();
        addParameter("collectionValue", ParameterType.READ_WRITE);
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        final Value collection = args.get(0);
        final Value element    = collection.removeLastMember(state);

        // write the reduced collection back into the outer environment
        writeBackVars.add(collection);

        return element;
    }
}

