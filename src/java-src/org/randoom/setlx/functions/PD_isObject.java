package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * isObject(Value) : Test if value-type is object.
 */
public class PD_isObject extends PreDefinedProcedure {

    private final static ParameterDef        VALUE      = createParameter("value");

    /** Definition of the PreDefinedProcedure `isObject'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_isObject();

    private PD_isObject() {
        super();
        addParameter(VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) {
        return args.get(VALUE).isObject();
    }
}

