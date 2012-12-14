package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// rational(stringOrNumber)      : convert string or number into a rational, returns om on failure

public class PD_rational extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_rational();

    private PD_rational() {
        super("rational");
        addParameter("value");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) {
        return args.get(0).toRational();
    }
}

