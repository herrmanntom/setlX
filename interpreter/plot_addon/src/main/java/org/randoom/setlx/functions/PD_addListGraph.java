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
            GRAPHNAME = createOptionalParameter(" graphname ", Rational.ONE);
    public final static PreDefinedProcedure
            DEFINITION = new PD_addListGraph();

    private PD_addListGraph() {
        super();
        addParameter(CANVAS);
        addParameter(VALUELIST);
        addParameter(GRAPHNAME);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        // addGraph(Canvas canvas, SetlList: values, [String: functionName] )

        // initialise parameter canvas, value list and functionName
        Canvas canvas = (Canvas) args.get(CANVAS);
        SetlList valueSetlList = (SetlList) args.get(VALUELIST);
        List valueList = ConvertSetlTypes.convertSetlList(valueSetlList);
        Value functionName = args.get(GRAPHNAME);

        //if the functionName is set
        if (!functionName.equalTo(Rational.ONE)) {
            SetlString graphNameString = (SetlString) functionName;
            return ConnectJFreeChart.getInstance().addListGraph(canvas, valueList, graphNameString.toString());
        }

        //if no optional parameter is set
        return ConnectJFreeChart.getInstance().addListGraph(canvas, valueList);
    }
}
