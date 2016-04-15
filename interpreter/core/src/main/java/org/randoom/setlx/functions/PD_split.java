package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * split(string, pattern) : Splits string at pattern into a list of strings.
 */
public class PD_split extends PreDefinedProcedure {

    private final static ParameterDefinition STRING     = createParameter("string");
    private final static ParameterDefinition PATTERN    = createParameter("pattern");

    /** Definition of the PreDefinedProcedure `split'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_split();

    private PD_split() {
        super();
        addParameter(STRING);
        addParameter(PATTERN);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        return args.get(STRING).split(state, args.get(PATTERN));
    }
}

