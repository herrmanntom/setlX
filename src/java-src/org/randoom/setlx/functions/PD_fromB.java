package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.List;

// fromB(rw collectionValue)     : selects and removes the first member from `collectionValue'

public class PD_fromB extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_fromB();

    private PD_fromB() {
        super("fromB");
        addParameter("collectionValue", ParameterDef.READ_WRITE);
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        final Value collection = args.get(0);
        final Value element    = collection.removeFirstMember();

        // write the reduced collection back into the outer environment
        writeBackVars.add(collection);

        return element;
    }
}

