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

public class PD_plot_addListGraph extends PreDefinedProcedure {


    private final static ParameterDefinition CANVAS = createParameter(" canvas ");
    private final static ParameterDefinition VALUELIST = createParameter(" valuelist ");
    private final static ParameterDefinition GRAPHNAME = createParameter(" graphname ");
    private final static ParameterDefinition GRAPHCOLOR = createOptionalParameter("graphcolor", Rational.ONE);
    private final static ParameterDefinition PLOTAREA = createOptionalParameter("plotarea", SetlBoolean.FALSE);

    public final static PreDefinedProcedure DEFINITION = new PD_plot_addListGraph();

    private PD_plot_addListGraph() {
        super();
        addParameter(CANVAS);
        addParameter(VALUELIST);
        addParameter(GRAPHNAME);
        addParameter(GRAPHCOLOR);
        addParameter(PLOTAREA);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {

        if (!PlotCheckType.isCanvas(args.get(CANVAS))) {
            throw new UndefinedOperationException("First parameter canvas has to be a Canvas object (eg. created with plot_createCanvas() )");
        }

        if(!PlotCheckType.isSetlListofSetlList(args.get(VALUELIST))){
            throw new UndefinedOperationException("Second parameter valuelist has to be a List of Lists (eq. [[1,2],[3,4],[5,6]])");
        }

        if(!PlotCheckType.isSetlString(args.get(GRAPHNAME))){
            throw new UndefinedOperationException("Third parameter graphname has to be a String (eq. \"Name of the Graph\" )");
        }

        if(!PlotCheckType.isSetlBoolean(args.get(PLOTAREA))){
            throw new UndefinedOperationException("Fifth parameter plotarea has to be a Boolean (eq. true)");
        }

        Canvas canvas = (Canvas) args.get(CANVAS);
        SetlList valueSetlList = (SetlList) args.get(VALUELIST);
        SetlString graphNameSetl = (SetlString)args.get(GRAPHNAME);
        String graphNameString = graphNameSetl.toString().replace("\"", "");

        if(!PlotCheckType.isSetlListofSetlListWithNumbers(valueSetlList)){
            throw new UndefinedOperationException("Second parameter valuelist has to be a List of Lists with Numbers (eq. [[1,2],[3,4],[5,6]])");
        }

        List<List<Double>> valueList = ConvertSetlTypes.convertSetlListToListOfListOfDouble(valueSetlList, state);
        Value graphColorV = args.get(GRAPHCOLOR);
        Value plotArea = args.get(PLOTAREA);

        boolean area = false;
        if (plotArea.equalTo(SetlBoolean.TRUE)) {
            area = true;
        }

        //if graphcolor is set
        if (!graphColorV.equalTo(Rational.ONE)) {

            if(!PlotCheckType.isSetlList(graphColorV)){
                throw new UndefinedOperationException("Fourth parameter graphcolor has to be a List (eq. [0,0,0])");
            }

            SetlList graphColorS = (SetlList) graphColorV;

            if(!PlotCheckType.isSetlListWithInteger(graphColorS)){
                throw new UndefinedOperationException("Fourth parameter graphcolor has to consist of Integer values (eq. [0,0,0])");
            }

            if (!(graphColorS.size() == 3)) {
                throw new UndefinedOperationException("Fourth parameter graphcolor has to consist of exactly three values (eq. [0,0,0])");
            }

            List<Integer> graphColor = ConvertSetlTypes.convertSetlListToListOfInteger(graphColorS);

            return ConnectJFreeChart.getInstance().addListGraph(canvas, valueList, graphNameString, graphColor, area);
        }

        //if no optional parameter is set
        List<Integer> graphColour = new ArrayList<>();
        graphColour.add(0);
        graphColour.add(0);
        graphColour.add(0);
        return ConnectJFreeChart.getInstance().addListGraph(canvas, valueList, graphNameString, graphColour, area);
    }
}
