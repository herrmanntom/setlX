package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// isList(value)           : test if value-type is list

public class PD_isList extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_isList();

    private PD_isList() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        return args.get(0).isList();
    }
}

