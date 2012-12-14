package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// shuffle(collectionValue)      : returns a randomly shuffled version of the collectionValue.

public class PD_shuffle extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_shuffle();

    private PD_shuffle() {
        super("shuffle");
        addParameter("collectionValue");
    }

    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        return args.get(0).shuffle();
    }

}

