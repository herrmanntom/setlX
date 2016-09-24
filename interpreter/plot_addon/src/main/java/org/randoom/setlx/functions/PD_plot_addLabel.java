package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.plot.types.Canvas;
import org.randoom.setlx.plot.utilities.ConnectJFreeChart;
import org.randoom.setlx.plot.utilities.ConvertSetlTypes;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.plot.utilities.PlotCheckType;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;

import java.util.HashMap;
import java.util.List;

public class PD_plot_addLabel extends PreDefinedProcedure {

    private final static ParameterDefinition CANVAS = createParameter("canvas");
    private final static ParameterDefinition XYTUPEL = createParameter("XYTupel");
    private final static ParameterDefinition LABEL = createParameter("Label");
    public final static PreDefinedProcedure DEFINITION = new PD_plot_addLabel();

    private PD_plot_addLabel() {
        super();
        addParameter(CANVAS);
        addParameter(XYTUPEL);
        addParameter(LABEL);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {

        if(!PlotCheckType.isCanvas(args.get(CANVAS))){
            throw new UndefinedOperationException("First parameter has to be a Canvas object");
        }

        if(!PlotCheckType.isSetlList(args.get(XYTUPEL))){
            throw new UndefinedOperationException("Second parameter has to be a SetlList (eq: [1,2]) ");
        }

        if(!PlotCheckType.isSetlString(args.get(LABEL))){
            throw new UndefinedOperationException("Third parameter hast do be a String (eq: \"Text on Label\")");
        }

        Canvas canvas = (Canvas)args.get(CANVAS);
        SetlList xyTupel = (SetlList)args.get(XYTUPEL);

        if(xyTupel.size() != 2){
            throw new UndefinedOperationException("Second parameter has to be a Tupel of Numbers (eq: [1,2]) ");
        }

        if(!PlotCheckType.isSetlListWithNumbers(xyTupel)){
            throw new UndefinedOperationException("Second parameter has to be a Tupel of Numbers (eq: [1,2]) ");
        }

        List<Double> list = ConvertSetlTypes.convertSetlListToListOfDouble(xyTupel, state);
        SetlString label = (SetlString)args.get(LABEL);
        String stringLabel = label.toString().replace("\"", "");

        return ConnectJFreeChart.getInstance().addLabel(canvas, list, stringLabel);
    }
}
