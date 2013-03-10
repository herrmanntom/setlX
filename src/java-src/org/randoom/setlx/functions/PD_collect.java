package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// collect(list)                 : Collects multiple occurrences of the same value in
//                                 `list' into a map of [value, nOccurences].

public class PD_collect extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_collect();

    private PD_collect() {
        super();
        addParameter("list");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        final Value list = args.get(0);
        if ( ! (list instanceof SetlList)) {
            throw new IncompatibleTypeException(
                "Argument '" + list + "' is not a list."
            );
        }
        return ((SetlList) list).collect(state);
    }
}

