package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.*;

import java.util.HashMap;

public class PD_plot_removeGraph extends PreDefinedProcedure {

    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef VALUE = createParameter("value");
    public final static PreDefinedProcedure DEFINITION = new PD_plot_removeGraph();
    private PD_plot_removeGraph(){
        super();
        addParameter(CANVAS);
        addParameter(VALUE);
    }
    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        Canvas c = (Canvas)args.get(CANVAS);
        Value v = args.get(VALUE);
        ConnectJFreeChart.getInstance().removeGraph(c, v);
        return new SetlString("Removed graph from canvas");
    }
}
