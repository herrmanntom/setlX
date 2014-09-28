package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * split(string, pattern) : Splits string at pattern into a list of strings.
 */
public class PD_split extends PreDefinedProcedure {

    private final static ParameterDef        STRING     = createParameter("string");
    private final static ParameterDef        PATTERN    = createParameter("pattern");

    /** Definition of the PreDefinedProcedure `split'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_split();

    private PD_split() {
        super();
        addParameter(STRING);
        addParameter(PATTERN);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) throws SetlException {
        return args.get(STRING).split(state, args.get(PATTERN));
    }
}

