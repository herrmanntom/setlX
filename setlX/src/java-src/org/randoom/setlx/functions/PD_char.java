package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;

import java.util.List;

// char(value)             : converts value into a single ascii character

public class PD_char extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_char();

    private PD_char() {
        super("char");
        addParameter("value");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        return args.get(0).charConvert();
    }
}

