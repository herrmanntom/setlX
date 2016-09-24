package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.plot.types.Canvas;
import org.randoom.setlx.plot.utilities.ConnectJFreeChart;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.plot.utilities.PlotCheckType;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;

import java.util.HashMap;

public class PD_plot_modScale extends PreDefinedProcedure {
    private final static ParameterDefinition CANVAS = createParameter("canvas");
    private final static ParameterDefinition XMINMAX = createParameter("xMinMax");
    private final static ParameterDefinition YMINMAX = createParameter("yMinMax");

    public final static PreDefinedProcedure DEFINITION = new PD_plot_modScale();

    private PD_plot_modScale(){
        super();
        addParameter(CANVAS);
        addParameter(XMINMAX);
        addParameter(YMINMAX);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {

        if(!PlotCheckType.isCanvas(args.get(CANVAS))){
            throw new UndefinedOperationException("First parameter has to be of object Canvas");
        }

        if(!PlotCheckType.isSetlList(args.get(XMINMAX))){
            throw new UndefinedOperationException("Second parameter xMinMax has to be a Tupel (eq. [2, 3])");
        }

        if(!PlotCheckType.isSetlList(args.get(YMINMAX))){
            throw new UndefinedOperationException("Third parameter yMinMax has to be a Tupel (eq. [2, 3])");
        }

        SetlList xList = (SetlList) args.get(XMINMAX);
        SetlList yList = (SetlList) args.get(YMINMAX);

        if(!PlotCheckType.isSetlListWithNumbers(xList)){
            throw new UndefinedOperationException("Second parameter xMinMax has to be a Tupel with Numbers (eq. [2,3])");
        }

        if(!PlotCheckType.isSetlListWithNumbers(yList)){
            throw new UndefinedOperationException("Third parameter xMinMax has to be a Tupel with Numbers (eq. [2,3])");
        }

        if(xList.size() != 2){
            throw new UndefinedOperationException("Second parameter xMinMax has to be a Tupel (eq. [2,3])");
        }

        if(yList.size() != 2){
            throw new UndefinedOperationException("Third parameter yMinMax has to be a Tupel (eq. [2,3])");
        }

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

        if(xMaxD < xMinD){
            throw new UndefinedOperationException("Second parameter: first element in Tupel has to be smaller than the second element");
        }

        if(yMaxD < yMinD){
            throw new UndefinedOperationException("Third parameter: first element in Tupel has to be smaller than the second element");
        }

        ConnectJFreeChart.getInstance().modScale((Canvas)args.get(CANVAS), xMinD, xMaxD, yMinD, yMaxD);
        return new SetlString("Set Scale to xMin,xMax: ("+xMinD+","+xMaxD+") and yMin,yMax: ("+yMinD+","+yMaxD+")");
    }
}
