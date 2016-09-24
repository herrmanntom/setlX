package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.plot.types.Canvas;
import org.randoom.setlx.plot.utilities.ConnectJFreeChart;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.plot.utilities.PlotCheckType;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;

import java.util.HashMap;

public class PD_plot_removeGraph extends PreDefinedProcedure {

    private final static ParameterDefinition CANVAS = createParameter("canvas");
    private final static ParameterDefinition VALUE = createParameter("value");
    public final static PreDefinedProcedure DEFINITION = new PD_plot_removeGraph();

    private PD_plot_removeGraph() {
        super();
        addParameter(CANVAS);
        addParameter(VALUE);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {

        if (!PlotCheckType.isCanvas(args.get(CANVAS))) {
            throw new UndefinedOperationException("First parameter has to be of object Canvas");
        }

        Canvas c = (Canvas) args.get(CANVAS);
        Value v = args.get(VALUE);
        ConnectJFreeChart.getInstance().removeGraph(c, v);
        return new SetlString("Removed graph from canvas");
    }
}
