package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Value;

import java.util.List;

// sort(collectionValue)         : returns a sorted version of collectionValue.

public class PD_sort extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_sort();

    private PD_sort() {
        super("sort");
        addParameter("collectionValue");
    }

    public Value execute(final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        return args.get(0).sort();
    }

}

