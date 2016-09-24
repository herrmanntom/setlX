package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.plot.types.Canvas;
import org.randoom.setlx.plot.utilities.ConnectJFreeChart;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.plot.utilities.PlotCheckType;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;

import java.util.HashMap;

public class PD_plot_legendVisible extends PreDefinedProcedure {

    private final static ParameterDefinition CANVAS = createParameter("canvas");
    private final static ParameterDefinition VISIBLE = createParameter("visible");

    public final static PreDefinedProcedure DEFINITION = new PD_plot_legendVisible();

    private PD_plot_legendVisible(){
        super();
        addParameter(CANVAS);
        addParameter(VISIBLE);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {

        if(!PlotCheckType.isCanvas(args.get(CANVAS))){
            throw new UndefinedOperationException("First parameter has to be of object Canvas");
        }

        if(!PlotCheckType.isSetlBoolean(args.get(VISIBLE))){
            throw new UndefinedOperationException("Second parameter visible has to be a Boolean");
        }

        Value canvas = args.get(CANVAS);
        Value visible = args.get(VISIBLE);
        boolean setVisible;

        if(visible.equalTo(SetlBoolean.TRUE) && visible.isBoolean().equalTo(SetlBoolean.TRUE)){
            setVisible = true;
        }else{
            setVisible = false;
        }

        ConnectJFreeChart.getInstance().legendVisible((Canvas)canvas, setVisible);
        if(setVisible){
            return new SetlString("Showing legend");
        }else{
            return new SetlString("Hiding legend");
        }

    }
}
