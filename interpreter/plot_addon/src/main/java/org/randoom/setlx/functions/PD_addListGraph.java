package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.*;
import org.randoom.setlx.utilities.*;

import java.util.HashMap;
import java.util.List;

public class PD_addListGraph extends PreDefinedProcedure {


    private final static ParameterDef
            CANVAS = createParameter(" canvas ");
    private final static ParameterDef
            VALUELIST = createParameter(" valuelist ");
    private final static ParameterDef
            GRAPHNAME = createParameter(" graphname ");
    private final static ParameterDef
            GRAPHCOLOR = createOptionalParameter("graphcolor", Rational.ONE);
    private final static ParameterDef
            PLOTAREA = createOptionalParameter("plotarea", Rational.ONE);

    public final static PreDefinedProcedure
            DEFINITION = new PD_addListGraph();

    private PD_addListGraph() {
        super();
        addParameter(CANVAS);
        addParameter(VALUELIST);
        addParameter(GRAPHNAME);
        addParameter(GRAPHCOLOR);
        addParameter(PLOTAREA);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        // addGraph(Canvas canvas, SetlList: values, [String: functionName] )

        // initialise parameter canvas, value list and functionName
        Canvas canvas = (Canvas) args.get(CANVAS);
        SetlList valueSetlList = (SetlList) args.get(VALUELIST);
        List valueList = ConvertSetlTypes.convertSetlList(valueSetlList);
        Value functionName = args.get(GRAPHNAME);
        SetlString graphNameSetl = (SetlString) functionName;
        String graphNameString = graphNameSetl.toString().replace("\"", "");

        Value graphColorV = args.get(GRAPHCOLOR);
        Value plotAreaV = args.get(PLOTAREA);

        //if graphcolor and plotarea are set
        if (!graphColorV.equalTo(Rational.ONE) && !plotAreaV.equalTo(Rational.ONE)) {

            SetlBoolean plotAreaS = (SetlBoolean)plotAreaV;
            SetlList graphColorS = (SetlList)graphColorV;

            if (!(graphColorS.size() == 3)) {
                return new SetlString("Parameter GRAPHCOLOR must have exactly three entrys");
            }

            boolean area;
            if (plotAreaS.equalTo(SetlBoolean.TRUE)) {
                area = true;
            } else {
                area = false;
            }

            List graphColor = ConvertSetlTypes.convertSetlList(graphColorS);

            return ConnectJFreeChart.getInstance().addListGraph(canvas, valueList, graphNameString, graphColor, area);
        }

        //if graphcolor is set
        if (!graphColorV.equalTo(Rational.ONE)) {

            SetlList graphColorS = (SetlList)graphColorV;

            if (!(graphColorS.size() == 3)) {
                return new SetlString("Parameter GRAPHCOLOR must have exactly three entrys");
            }

            List graphColor = ConvertSetlTypes.convertSetlList(graphColorS);

            return ConnectJFreeChart.getInstance().addListGraph(canvas, valueList, graphNameString, graphColor);
        }

        //if no optional parameter is set
        return ConnectJFreeChart.getInstance().addListGraph(canvas, valueList, graphNameString);
    }
}
