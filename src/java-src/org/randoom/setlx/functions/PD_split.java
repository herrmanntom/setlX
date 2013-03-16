package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// split(string, pattern)        : splits string at pattern into a list of strings

public class PD_split extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_split();

    private PD_split() {
        super();
        addParameter("string");
        addParameter("pattern");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).split(state, args.get(1));
    }
}

