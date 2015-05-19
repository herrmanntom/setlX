package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Canvas;
import org.randoom.setlx.utilities.ConnectJFreeChart;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

public class PD_addParamGraph extends PreDefinedProcedure {

    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef XFUNCTION = createParameter("xFunction");
    private final static ParameterDef YFUNCTION = createParameter("yFunction");
    private final static ParameterDef GRAPHNAME = createOptionalParameter("graphname", Rational.ONE);
    private final static ParameterDef PLOTAREA = createOptionalParameter("plotArea", Rational.ONE);
    public final static PreDefinedProcedure DEFINITION = new PD_addParamGraph();
    private PD_addParamGraph(){
        super();
        addParameter(CANVAS);
        addParameter(XFUNCTION);
        addParameter(YFUNCTION);
        addParameter(GRAPHNAME);
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
        Value functionName = args.get(GRAPHNAME);
        Value plotarea = args.get(PLOTAREA);

        // if functionName and plotArea are set
        if (!functionName.equalTo(Rational.ONE) && !plotarea.equalTo(Rational.ONE)) {
            SetlString graphNameString = (SetlString) functionName;
            SetlBoolean plotAreaBool = (SetlBoolean) plotarea;
            boolean area;
            if (plotAreaBool.equalTo(SetlBoolean.TRUE)) {
                area = true;
            } else {
                area = false;
            }
            return ConnectJFreeChart.getInstance().addParamGraph(canvas, xFunction, yFunction, graphNameString.toString().replace("\"", ""), area, "black");
        }

        //if only the functionName is set
        if (!functionName.equalTo(Rational.ONE)) {
            SetlString graphNameString = (SetlString) functionName;
            return ConnectJFreeChart.getInstance().addParamGraph(canvas, xFunction, yFunction, graphNameString.toString().replace("\"", ""), );
        }

        //if only the plotarea is set
        if (!plotarea.equalTo(Rational.ONE)) {
            SetlBoolean plotAreaBool = (SetlBoolean) plotarea;
            boolean area;
            if (plotAreaBool.equalTo(SetlBoolean.TRUE)) {
                area = true;
            } else {
                area = false;
            }
            return ConnectJFreeChart.getInstance().addParamGraph(canvas, xFunction, yFunction, area, "black");
        }

        //if no optional parameter is set
        return ConnectJFreeChart.getInstance().addParamGraph(canvas, xFunction, yFunction);
    }
}
