package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.*;

import java.util.HashMap;

public class PD_removeGraph extends PreDefinedProcedure {

    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef GRAPH = createParameter("graph");
    public final static PreDefinedProcedure DEFINITION = new PD_removeGraph();
    private PD_removeGraph(){
        super();
        addParameter(CANVAS);
        addParameter(GRAPH);
    }
    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        Canvas c = (Canvas)args.get(CANVAS);
        Graph g = (Graph)args.get(GRAPH);
        ConnectJMathPlot.getInstance().removeGraph(c, g);
        return new SetlString("Removed "+g+" from "+c);
    }
}
