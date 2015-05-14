package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

public class PD_addParamGraph extends PreDefinedProcedure {

    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef XFUNCTION = createParameter("xFunction");
    private final static ParameterDef YFUNCTION = createParameter("yFunction");
    private final static ParameterDef NAME = createOptionalParameter("name", Rational.ONE);
    private final static ParameterDef PLOTAREA = createOptionalParameter("plotArea", Rational.ONE);
    public final static PreDefinedProcedure DEFINITION = new PD_addParamGraph();
    private PD_addParamGraph(){
        super();
        addParameter(CANVAS);
        addParameter(XFUNCTION);
        addParameter(YFUNCTION);
        addParameter(NAME);
        addParameter(PLOTAREA);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        //Todo: implement call connector class
        return new SetlString(String.valueOf(args.entrySet()));
    }
}
