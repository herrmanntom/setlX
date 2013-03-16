package org.randoom.setlx.functions;

import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// canonical(term)         : returns a string of a term in its true form

public class PD_canonical extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_canonical();

    private PD_canonical() {
        super();
        addParameter("term");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        final StringBuilder sb = new StringBuilder();
        args.get(0).canonical(state, sb);
        return SetlString.newSetlStringFromSB(sb);
    }
}

