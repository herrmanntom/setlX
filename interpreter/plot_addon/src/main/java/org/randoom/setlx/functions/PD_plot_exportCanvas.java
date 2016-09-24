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

public class PD_plot_exportCanvas extends PreDefinedProcedure {

    private final static ParameterDefinition CANVAS = createParameter("canvas");
    private final static ParameterDefinition PATH = createParameter("path");
    public final static PreDefinedProcedure DEFINITION = new PD_plot_exportCanvas();

    private PD_plot_exportCanvas() {
        super();
        addParameter(CANVAS);
        addParameter(PATH);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        if(!PlotCheckType.isCanvas(args.get(CANVAS))){
            throw new UndefinedOperationException("First parameter has to be of object Canvas");
        }

        if(!PlotCheckType.isSetlString(args.get(PATH))){
            throw new UndefinedOperationException("Second parameter has to be a String");
        }

        Canvas canvas = (Canvas)args.get(CANVAS);
        SetlString path = (SetlString)args.get(PATH);
        String pathString = state.filterFileName(path.getUnquotedString(state));
        ConnectJFreeChart.getInstance().exportCanvas(canvas, pathString);
        return new SetlString("Exported Canvas to "+pathString);
    }
}
