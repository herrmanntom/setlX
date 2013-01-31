package org.randoom.setlx.functions;

import org.randoom.setlx.types.SetlObject;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// new()                   : create a new object

public class PD_new extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_new();

    private PD_new() {
        super("new");
    }

    @Override
    public SetlObject execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        return new SetlObject();
    }
}

