package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// join(collection, separator)   : returns a string with all the members in `collection' separated by `separator'.

public class PD_join extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_join();

    private PD_join() {
        super();
        addParameter("collection");
        addParameter("separator");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {

        return args.get(0).join(state, args.get(1));

    }

}

