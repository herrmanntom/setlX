package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Canvas;
import org.randoom.setlx.utilities.ConnectJFreeChart;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

public class PD_plot_legendVisible extends PreDefinedProcedure {

    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef VISIBLE = createParameter("visible");

    public final static PreDefinedProcedure DEFINITION = new PD_plot_legendVisible();

    private PD_plot_legendVisible(){
        super();
        addParameter(CANVAS);
        addParameter(VISIBLE);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
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
