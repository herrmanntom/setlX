package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

public class PD_insertLabel extends PreDefinedProcedure {

    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef XLABEL = createParameter("xLabel");
    private final static ParameterDef YLABEL = createParameter("yLabel");
    public final static PreDefinedProcedure DEFINITION = new PD_insertLabel();

    private PD_insertLabel() {
        super();
        addParameter(CANVAS);
        addParameter(XLABEL);
        addParameter(YLABEL);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        return new SetlString(String.valueOf(args.entrySet()));
    }
}
