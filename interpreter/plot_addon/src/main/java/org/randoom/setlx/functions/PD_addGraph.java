package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.*;

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
        Canvas canvas = (Canvas)args.get(CANVAS);
        SetlString functionDefinition = (SetlString)args.get(FUNCTIONDEFINITION);
        String function = functionDefinition.toString();
        Value graphname = args.get(GRAPHNAME);
        Value plotarea = args.get(PLOTAREA);
        Graph g;

        if(!graphname.equalTo(Rational.ONE) && !plotarea.equalTo(Rational.ONE)){
            SetlString graphNameString = (SetlString)graphname;
            SetlBoolean plotAreaBool = (SetlBoolean)plotarea;
            boolean area;
            if(plotAreaBool.equalTo(SetlBoolean.TRUE)){
                area = true;
            }
            else{
                area = false;
            }
            return ConnectJMathPlot.getInstance().addGraph(canvas, function,graphNameString.toString(),area );
        }
        if(!graphname.equalTo(Rational.ONE)){
            SetlString graphNameString = (SetlString)graphname;
            return ConnectJMathPlot.getInstance().addGraph(canvas, function,graphNameString.toString() );
        }
        if(!plotarea.equalTo(Rational.ONE)){
            SetlBoolean plotAreaBool = (SetlBoolean)plotarea;
            boolean area;
            if(plotAreaBool.equalTo(SetlBoolean.TRUE)){
                area = true;
            }
            else{
                area = false;
            }
            return ConnectJMathPlot.getInstance().addGraph(canvas, function,area );
        }

        return ConnectJMathPlot.getInstance().addGraph(canvas, functionDefinition.toString());
    }
}
