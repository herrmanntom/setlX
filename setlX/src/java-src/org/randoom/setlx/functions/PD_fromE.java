package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;

import java.util.List;

// fromE(rw collectionValue)     : selects and removes the last member from `collectionValue'

public class PD_fromE extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_fromE();

    private PD_fromE() {
        super("fromE");
        addParameter("collectionValue", ParameterDef.READ_WRITE);
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        Value collection = args.get(0);
        Value element    = collection.removeLastMember();

        // write the reduced collection back into the outer environment
        writeBackVars.add(collection);

        return element;
    }
}

