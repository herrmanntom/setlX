package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.*;
import org.randoom.setlx.utilities.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PD_plot_addParamGraph extends PreDefinedProcedure {

    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef XFUNCTION = createParameter("xFunction");
    private final static ParameterDef YFUNCTION = createParameter("yFunction");
    private final static ParameterDef GRAPHNAME = createParameter("graphname");
    private final static ParameterDef PARAMBOUND = createParameter("ParameterBounds");
    private final static ParameterDef GRAPHCOLOR = createOptionalParameter("graphcolor (RGB)", Rational.ONE);
    private final static ParameterDef PLOTAREA = createOptionalParameter("plotArea", Rational.ONE);
    public final static PreDefinedProcedure DEFINITION = new PD_plot_addParamGraph();
    private PD_plot_addParamGraph(){
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
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        // addParamGraph(Canvas canvas, String: xFunction, String: yFunction, [String: functionName], [Boolean: plotArea] )

        // initialise parameter canvas, xFunction, yFunction, functionName and plotArea
        Canvas canvas = (Canvas) args.get(CANVAS);
        SetlString xFunctionDefinition = (SetlString) args.get(XFUNCTION);
        SetlString yFunctionDefinition = (SetlString) args.get(YFUNCTION);
        // function comes as ""sin(x)"" so i have to replace the quotation marks
        String xFunction = xFunctionDefinition.toString().replace("\"", "");
        String yFunction = yFunctionDefinition.toString().replace("\"", "");
        Value graphNameV = args.get(GRAPHNAME);
        SetlString graphNameS = (SetlString) graphNameV;
        String graphName = graphNameS.toString().replace("\"", "");
        SetlList limitsV = (SetlList)args.get(PARAMBOUND);
        List<Double> limitsList = ConvertSetlTypes.convertSetlListAsDouble(limitsV);

        Value graphColorV = args.get(GRAPHCOLOR);
        Value plotarea = args.get(PLOTAREA);

        // if graphcolor and plotArea are set
        if (!graphColorV.equalTo(Rational.ONE) && !plotarea.equalTo(Rational.ONE)) {
            SetlList graphColorS = (SetlList) args.get(GRAPHCOLOR);
            if(!(graphColorS.size()==3)){
                return new SetlString("Parameter graphcolor have to consits of exact three values (RGB)");
            }
            List<Integer> graphColor = ConvertSetlTypes.convertSetlListAsInteger(graphColorS);

            SetlBoolean plotAreaBool = (SetlBoolean) plotarea;
            boolean area;
            if (plotAreaBool.equalTo(SetlBoolean.TRUE)) {
                area = true;
            } else {
                area = false;
            }
            return ConnectJFreeChart.getInstance().addParamGraph(canvas, xFunction, yFunction, graphName, graphColor, area, limitsList);
        }

        //if only the graphcolor is set
        if (!graphColorV.equalTo(Rational.ONE)) {
            SetlList graphColorS = (SetlList) args.get(GRAPHCOLOR);
            if(!(graphColorS.size()==3)){
                return new SetlString("Parameter graphcolor have to consits of exact three values");
            }
            List<Integer> graphColor = ConvertSetlTypes.convertSetlListAsInteger(graphColorS);
            return ConnectJFreeChart.getInstance().addParamGraph(canvas, xFunction, yFunction, graphName, graphColor, limitsList);
        }


        //if no optional parameter is set
        return ConnectJFreeChart.getInstance().addParamGraph(canvas, xFunction, yFunction, graphName, limitsList);
    }
}
