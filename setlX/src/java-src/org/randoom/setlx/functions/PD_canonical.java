package org.randoom.setlx.functions;

import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;

import java.util.List;

// canonical(term)         : returns a string of a term in its true form

public class PD_canonical extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_canonical();

    private PD_canonical() {
        super("canonical");
        addParameter("term");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        final StringBuilder sb = new StringBuilder();
        args.get(0).canonical(sb);
        return new SetlString(sb.toString());
    }
}

