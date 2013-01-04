package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// sort(collectionValue)         : returns a sorted version of collectionValue.

public class PD_sort extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_sort();

    private PD_sort() {
        super("sort");
        addParameter("collectionValue");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        return args.get(0).sort();
    }

}

