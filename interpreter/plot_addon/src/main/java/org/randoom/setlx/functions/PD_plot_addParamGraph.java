package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.plot.types.Canvas;
import org.randoom.setlx.plot.utilities.ConnectJFreeChart;
import org.randoom.setlx.plot.utilities.ConvertSetlTypes;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.plot.utilities.PlotCheckType;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.types.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PD_plot_addParamGraph extends PreDefinedProcedure {

    private final static ParameterDefinition CANVAS = createParameter("canvas");
    private final static ParameterDefinition XFUNCTION = createParameter("xFunction");
    private final static ParameterDefinition YFUNCTION = createParameter("yFunction");
    private final static ParameterDefinition GRAPHNAME = createParameter("graphname");
    private final static ParameterDefinition PARAMBOUND = createParameter("ParameterBounds");
    private final static ParameterDefinition GRAPHCOLOR = createOptionalParameter("graphcolor (RGB)", Rational.ONE);
    private final static ParameterDefinition PLOTAREA = createOptionalParameter("plotArea", SetlBoolean.FALSE);
    public final static PreDefinedProcedure DEFINITION = new PD_plot_addParamGraph();

    private PD_plot_addParamGraph() {
        super();
        addParameter(CANVAS);
        addParameter(XFUNCTION);
        addParameter(YFUNCTION);
        addParameter(GRAPHNAME);
        addParameter(PARAMBOUND);
        addParameter(GRAPHCOLOR);
        addParameter(PLOTAREA);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {

        if (!PlotCheckType.isCanvas(args.get(CANVAS))) {
            throw new UndefinedOperationException("First parameter canvas has to be a Canvas object (eg. created with plot_createCanvas() )");
        }

        if (!PlotCheckType.isSetlString(args.get(XFUNCTION))) {
            throw new UndefinedOperationException("Second parameter xFunction has to be a String (eq. \"x+2\")");
        }

        if (!PlotCheckType.isSetlString(args.get(YFUNCTION))) {
            throw new UndefinedOperationException("Third parameter yFunction has to be a String (eq. \"x+2\")");
        }

        if (!PlotCheckType.isSetlString(args.get(GRAPHNAME))) {
            throw new UndefinedOperationException("Fourth parameter graphname has to be a String (eq. \"Name of the Graph\" )");
        }

        if (!PlotCheckType.isSetlList(args.get(PARAMBOUND))) {
            throw new UndefinedOperationException("Fifth parameter ParameterBounds has to be a List (eq. [-2, 3])");
        }

        if (!PlotCheckType.isSetlBoolean(args.get(PLOTAREA))) {
            throw new UndefinedOperationException("Seventh parameter plotarea has to be a Boolean (eq. true)");
        }

        // initialise parameter canvas, xFunction, yFunction, functionName and plotArea
        Canvas canvas = (Canvas) args.get(CANVAS);
        SetlString xFunctionDefinition = (SetlString) args.get(XFUNCTION);
        SetlString yFunctionDefinition = (SetlString) args.get(YFUNCTION);
        // function comes as ""sin(x)"" so i have to replace the quotation marks
        String xFunction = xFunctionDefinition.toString().replace("\"", "");
        String yFunction = yFunctionDefinition.toString().replace("\"", "");
        SetlString graphNameS = (SetlString) args.get(GRAPHNAME);
        String graphName = graphNameS.toString().replace("\"", "");
        SetlList limitsV = (SetlList) args.get(PARAMBOUND);

        if (limitsV.size() != 2) {
            throw new UndefinedOperationException("Fifth parameter ParameterBounds has to be a Tupel (eq. [-2, 3])");
        }

        if (!PlotCheckType.isSetlListWithNumbers(limitsV)) {
            throw new UndefinedOperationException("Fifth parameter ParameterBounds has to consist of Numbers (eq. [-1, 3])");
        }

        List<Double> limitsList = ConvertSetlTypes.convertSetlListToListOfDouble(limitsV, state);

        Value graphColorV = args.get(GRAPHCOLOR);
        Value plotArea = args.get(PLOTAREA);

        boolean area = false;
        if (plotArea.equalTo(SetlBoolean.TRUE)) {
            area = true;
        }
        //if only the graphcolor is set
        if (!graphColorV.equalTo(Rational.ONE)) {
            if (!PlotCheckType.isSetlList(graphColorV)) {
                throw new UndefinedOperationException("Sixth parameter graphcolor has to be a List (eq. [0,0,0])");
            }

            SetlList graphColorS = (SetlList) graphColorV;

            if (!PlotCheckType.isSetlListWithInteger(graphColorS)) {
                throw new UndefinedOperationException("Sixth parameter graphcolor has to consist of Integer values (eq. [0,0,0])");
            }

            if (!(graphColorS.size() == 3)) {
                throw new UndefinedOperationException("Sixth parameter graphcolor has to consist of exactly three values (eq. [0,0,0])");
            }

            List<Integer> graphColor = ConvertSetlTypes.convertSetlListToListOfInteger(graphColorS);
            return ConnectJFreeChart.getInstance().addParamGraph(canvas, xFunction, yFunction, graphName, state, graphColor, area, limitsList);
        }


        //if no optional parameter is set
        List<Integer> graphColor = new ArrayList<>();
        graphColor.add(0);
        graphColor.add(0);
        graphColor.add(0);
        return ConnectJFreeChart.getInstance().addParamGraph(canvas, xFunction, yFunction, graphName, state, graphColor, area, limitsList);
    }
}
