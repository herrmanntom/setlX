package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// char(value)             : converts value into a single ascii character

public class PD_char extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_char();

    private PD_char() {
        super("char");
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).charConvert();
    }
}

