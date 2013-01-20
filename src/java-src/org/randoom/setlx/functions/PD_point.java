package org.randoom.setlx.functions;


import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.StdDraw;

public class PD_point extends StdDrawFunction {
    public final static PreDefinedFunction DEFINITION = new PD_point();
    
    public PD_point(){
        super("point");
        addParameter("x0");
        addParameter("y0");
    }
    

    @Override
    protected Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException{
        StdDraw.point(doubleFromValue(args.get(0)),doubleFromValue( args.get(1)));    
        return SetlBoolean.TRUE;
    }
}