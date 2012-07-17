package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Value;

import java.util.List;

// join(collection, separator)   : returns a string with all the members in `collection' separated by `separator'.

public class PD_join extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_join();

    private PD_join() {
        super("join");
        addParameter("collection");
        addParameter("separator");
    }

    public Value execute(final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {

        return args.get(0).join(args.get(1));

    }

}

