package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Canvas;
import org.randoom.setlx.utilities.ConnectJFreeChart;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

public class PD_plot_modScale extends PreDefinedProcedure {
    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef XMINMAX = createParameter("xMinMax");
    private final static ParameterDef YMINMAX = createParameter("yMinMax");

    public final static PreDefinedProcedure DEFINITION = new PD_plot_modScale();

    private PD_plot_modScale(){
        super();
        addParameter(CANVAS);
        addParameter(XMINMAX);
        addParameter(YMINMAX);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        SetlList xList = (SetlList) args.get(XMINMAX);
        SetlList yList = (SetlList) args.get(YMINMAX);

        Value xMinV = xList.firstMember();
        Value xMaxV = xList.lastMember();
        Value yMinV = yList.firstMember();
        Value yMaxV = yList.lastMember();
        double xMaxD;
        double xMinD;
        double yMaxD;
        double yMinD;

        if (xMaxV.isInteger().equalTo(SetlBoolean.TRUE)) {
            xMaxD = (double) xMaxV.jIntValue();
        } else {
            xMaxD = xMaxV.jDoubleValue();
        }
        if (xMinV.isInteger().equalTo(SetlBoolean.TRUE)) {
            xMinD = (double) xMinV.jIntValue();
        } else {
            xMinD = xMinV.jDoubleValue();
        }
        if (yMaxV.isInteger().equalTo(SetlBoolean.TRUE)) {
            yMaxD = (double) yMaxV.jIntValue();
        } else {
            yMaxD = yMaxV.jDoubleValue();
        }
        if (yMinV.isInteger().equalTo(SetlBoolean.TRUE)) {
            yMinD = (double) yMinV.jIntValue();
        } else {
            yMinD = yMinV.jDoubleValue();
        }

        ConnectJFreeChart.getInstance().modScale((Canvas)args.get(CANVAS), xMinD, xMaxD, yMinD, yMaxD);
        return new SetlString("Set Scale to xMinMax: ("+xMinD+","+xMaxD+") and yMinMax: ("+yMinD+","+yMaxD+")");
    }
}
