package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * range(map) : Get range of map.
 */
public class PD_range extends PreDefinedProcedure {

    private final static ParameterDef        MAP        = createParameter("map");

    /** Definition of the PreDefinedProcedure `range'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_range();

    private PD_range() {
        super();
        addParameter(MAP);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) throws SetlException {
        return args.get(MAP).range(state);
    }
}

