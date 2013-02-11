package org.randoom.setlx.functions;


import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_filledPolygon extends StdDrawPolygonFunction {
    public final static PreDefinedFunction DEFINITION = new PD_filledPolygon();
    
    public PD_filledPolygon(){
        super("filledPolygon");
        addParameter("x");
        addParameter("y");
    }
    

    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException{
        StdDraw.filledPolygon( doubleArrayFromValue(args.get(0)), doubleArrayFromValue(args.get(1)));    
        return SetlBoolean.TRUE;
    }
}