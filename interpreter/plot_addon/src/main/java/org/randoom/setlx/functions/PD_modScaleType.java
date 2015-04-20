package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

public class PD_modScaleType extends PreDefinedProcedure {

    private final static ParameterDef XTYPE = createParameter("xType");

    private final static ParameterDef YTYPE = createParameter("yType");

    public final static PreDefinedProcedure DEFINITION = new PD_modScaleType();

    private PD_modScaleType(){
        super();
        addParameter(XTYPE);
        addParameter(YTYPE);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        return new SetlString(String.valueOf(args.entrySet()));
    }
}
