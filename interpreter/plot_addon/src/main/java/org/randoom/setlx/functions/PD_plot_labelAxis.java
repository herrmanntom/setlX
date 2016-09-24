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

public class PD_plot_labelAxis extends PreDefinedProcedure {

    private final static ParameterDefinition CANVAS = createParameter("canvas");
    private final static ParameterDefinition XLABEL = createParameter("xLabel");
    private final static ParameterDefinition YLABEL = createParameter("yLabel");
    public final static PreDefinedProcedure DEFINITION = new PD_plot_labelAxis();

    private PD_plot_labelAxis() {
        super();
        addParameter(CANVAS);
        addParameter(XLABEL);
        addParameter(YLABEL);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {

        if(!PlotCheckType.isCanvas(args.get(CANVAS))){
            throw new UndefinedOperationException("First parameter has to be of object Canvas");
        }


        if(!PlotCheckType.isSetlString(args.get(XLABEL))){
            throw new UndefinedOperationException("Second parameter xLabel has to be a String");
        }

        if(!PlotCheckType.isSetlString(args.get(YLABEL))){
            throw new UndefinedOperationException("Thrid parameter yLabel has to be a String");
        }

        Value canvas = args.get(CANVAS);
        Value xLabel = args.get(XLABEL);
        Value yLabel = args.get(YLABEL);
        ConnectJFreeChart.getInstance().labelAxis((Canvas) canvas, xLabel.toString().replace("\"", ""), yLabel.toString().replace("\"", ""));
        return new SetlString("Added xLabel \""+xLabel+"\" and yLabel \""+yLabel+"\" to Canvas");
    }
}
