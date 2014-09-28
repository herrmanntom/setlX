package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * isList(value) : Test if value-type is list.
 */
public class PD_isList extends PreDefinedProcedure {

    private final static ParameterDef        VALUE      = createParameter("value");

    /** Definition of the PreDefinedProcedure `isList'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_isList();

    private PD_isList() {
        super();
        addParameter(VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) {
        return args.get(VALUE).isList();
    }
}

