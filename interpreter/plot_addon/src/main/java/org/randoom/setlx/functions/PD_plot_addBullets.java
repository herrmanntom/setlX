package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.plot.types.Canvas;
import org.randoom.setlx.plot.utilities.ConnectJFreeChart;
import org.randoom.setlx.plot.utilities.ConvertSetlTypes;
import org.randoom.setlx.plot.utilities.PlotCheckType;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PD_plot_addBullets extends PreDefinedProcedure {

    private final static ParameterDefinition CANVAS = createParameter("canvas");
    private final static ParameterDefinition XYTUPEL = createParameter("xyTupel");
    private final static ParameterDefinition RGBLIST = createOptionalParameter("RGBList", Rational.ONE);
    private final static ParameterDefinition BULLETSIZE = createOptionalParameter("Bulletsize", SetlDouble.FIVE);
    public final static PreDefinedProcedure DEFINITION = new PD_plot_addBullets();

    private PD_plot_addBullets() {
        super();
        addParameter(CANVAS);
        addParameter(XYTUPEL);
        addParameter(RGBLIST);
        addParameter(BULLETSIZE);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        Canvas canvas;
        SetlList xylist;

        if (!(PlotCheckType.isCanvas(args.get(CANVAS)))) {
            throw new UndefinedOperationException("First parameter has to be a canvas object. (eq. created with plot_createCanvas() )");
        }

        if(!(PlotCheckType.isSetlListofSetlList(args.get(XYTUPEL)))){
            throw new UndefinedOperationException("Second parameter has to be a List with Tupel (eq. [[-1,2], [4,5], [6.5, -3]] )");
        }

        if(!(PlotCheckType.isSetlNumber(args.get(BULLETSIZE)))){
            throw new UndefinedOperationException("Optional parameter Bulletsize has to be a Number (eq. 5.0)");
        }

        xylist = (SetlList) args.get(XYTUPEL);
        if(!(PlotCheckType.isSetlListWithTupel(xylist))){
            throw new UndefinedOperationException("Second parameter has to be a List with Tupel (eq. [[-1,2], [4,5], [6.5, -3]] )");
        }

        if(!(PlotCheckType.isSetlListofSetlListWithNumbers(xylist))){
            throw new UndefinedOperationException("Tupel in second parameter have to be Numbers (Integer or Doubles) ");
        }

        List<List<Double>> bulletList = ConvertSetlTypes.convertSetlListToListOfListOfDouble(xylist, state);
        double bSize = ConvertSetlTypes.convertNumberToDouble(args.get(BULLETSIZE));


        //if the color parameter is set
        if (!args.get(RGBLIST).equalTo(Rational.ONE)) {
            if(!PlotCheckType.isSetlList(args.get(RGBLIST))){
                throw new UndefinedOperationException("Optional third parameter has to be a List (eq. [0,0,0]");
            }
            SetlList rgblistSetl = (SetlList) args.get(RGBLIST);
            if (!(rgblistSetl.size() == 3)) {
                throw new UndefinedOperationException("Parameter RGBLIST must have exactly three entrys (eq: [0,0,0])");
            }
            List<Integer> rgblist = ConvertSetlTypes.convertSetlListToListOfInteger(rgblistSetl);

            return ConnectJFreeChart.getInstance().addBullets((Canvas) args.get(CANVAS), bulletList, rgblist, bSize);
        }

        //if the color parameter is not set
        List<Integer> colorList = new ArrayList<>();
        colorList.add(0);
        colorList.add(0);
        colorList.add(0);
        return ConnectJFreeChart.getInstance().addBullets((Canvas) args.get(CANVAS), bulletList, colorList , bSize);
    }
}
