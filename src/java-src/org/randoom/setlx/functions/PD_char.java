package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * char(value) : Converts value into a single ASCII character.
 */
public class PD_char extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `char'. */
    public final static PreDefinedProcedure DEFINITION = new PD_char();

    private PD_char() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return args.get(0).charConvert(state);
    }
}

