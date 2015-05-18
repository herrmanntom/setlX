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

public class PD_addGraph extends PreDefinedProcedure {


    private final static ParameterDef
            CANVAS = createParameter(" canvas ");
    private final static ParameterDef
            FUNCTIONDEFINITION = createParameter(" functiondefinition ");
    private final static ParameterDef
            GRAPHNAME = createOptionalParameter(" graphname ", Rational.ONE);
    private final static ParameterDef
            PLOTAREA = createOptionalParameter(" plotarea ", Rational.ONE);
    public final static PreDefinedProcedure
            DEFINITION = new PD_addGraph();

    private PD_addGraph() {
        super();
        addParameter(CANVAS);
        addParameter(FUNCTIONDEFINITION);
        addParameter(GRAPHNAME);
        addParameter(PLOTAREA);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        // addGraph(Canvas canvas, String: function, [String: functionName], [Boolean: plotArea] )

        // initialise parameter canvas, function, functionName and plotArea
        Canvas canvas = (Canvas) args.get(CANVAS);
        SetlString functionDefinition = (SetlString) args.get(FUNCTIONDEFINITION);
        // function comes as ""sin(x)"" so i have to replace the quotation marks
        String function = functionDefinition.toString().replace("\"", "");
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
            return ConnectJFreeChart.getInstance().addGraph(canvas, function, graphNameString.toString().replace("\"", ""), area);
        }

        //if only the functionName is set
        if (!functionName.equalTo(Rational.ONE)) {
            SetlString graphNameString = (SetlString) functionName;
            return ConnectJFreeChart.getInstance().addGraph(canvas, function, graphNameString.toString().replace("\"", ""));
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
            return ConnectJFreeChart.getInstance().addGraph(canvas, function, area);
        }

        //if no optional parameter is set
        return ConnectJFreeChart.getInstance().addGraph(canvas, function);
    }
}
