package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ConnectJMathPlot;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

public class PD_addChart extends PreDefinedProcedure {

    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef CHARTTYPE = createParameter("chartType");
    private final static ParameterDef VALUES = createParameter("values");
    private final static ParameterDef NAME = createOptionalParameter("name", Rational.ONE);
    public final static PreDefinedProcedure DEFINITION = new PD_addChart();
    private PD_addChart(){
        super();
        addParameter(CANVAS);
        addParameter(CHARTTYPE);
        addParameter(VALUES);
        addParameter(NAME);
    }
    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        //TODO: function call to Connector Class
        return new SetlString(String.valueOf(args.entrySet()));
    }
}
