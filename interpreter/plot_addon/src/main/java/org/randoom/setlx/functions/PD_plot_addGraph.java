package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.*;
import org.randoom.setlx.utilities.*;

import java.util.HashMap;
import java.util.List;

public class PD_plot_addGraph extends PreDefinedProcedure {


    private final static ParameterDef
            CANVAS = createParameter(" canvas ");
    private final static ParameterDef
            FUNCTIONDEFINITION = createParameter(" functiondefinition ");
    private final static ParameterDef
            GRAPHNAME = createParameter(" graphname ");
    private final static ParameterDef GRAPHCOLOR = createOptionalParameter("graphcolor", Rational.ONE);
    private final static ParameterDef
            PLOTAREA = createOptionalParameter(" plotarea ", Rational.ONE);

    public final static PreDefinedProcedure
            DEFINITION = new PD_plot_addGraph();

    private PD_plot_addGraph() {
        super();
        addParameter(CANVAS);
        addParameter(FUNCTIONDEFINITION);
        addParameter(GRAPHNAME);
        addParameter(GRAPHCOLOR);
        addParameter(PLOTAREA);

    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {

        // initialise parameter canvas, function, functionName and plotArea
        Canvas canvas = (Canvas) args.get(CANVAS);
        SetlString functionDefinition = (SetlString) args.get(FUNCTIONDEFINITION);
        Value functionName = args.get(GRAPHNAME);
        Value plotarea = args.get(PLOTAREA);
        Value colorListValue = args.get(GRAPHCOLOR);

        // function comes as ""sin(x)"" so i have to replace the quotation marks
        String function = functionDefinition.toString().replace("\"", "");
        String graphNameString = functionName.toString().replace("\"", "");
        // if graphcolor and plotArea are set
        if (!colorListValue.equalTo(Rational.ONE) && !plotarea.equalTo(Rational.ONE)) {
            SetlList colorListSetl = (SetlList) colorListValue;
            List<Integer> colorList = ConvertSetlTypes.convertSetlListAsInteger(colorListSetl);
            SetlBoolean plotAreaBool = (SetlBoolean) plotarea;
            boolean area;
            if (plotAreaBool.equalTo(SetlBoolean.TRUE)) {
                area = true;
            } else {
                area = false;
            }
            return ConnectJFreeChart.getInstance().addGraph(canvas, function, graphNameString, state , colorList, area);
        }

        //if graphcolor is set
        if (!colorListValue.equalTo(Rational.ONE)) {
            SetlList colorListSetl = (SetlList) colorListValue;
            List<Integer> colorList = ConvertSetlTypes.convertSetlListAsInteger(colorListSetl);
            return ConnectJFreeChart.getInstance().addGraph(canvas, function, graphNameString, state , colorList);
        }

        //if no optional parameter is set
        return ConnectJFreeChart.getInstance().addGraph(canvas, function, graphNameString, state );
    }
}
