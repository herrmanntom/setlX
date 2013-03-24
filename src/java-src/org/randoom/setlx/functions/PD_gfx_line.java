package org.randoom.setlx.functions;


import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_gfx_line extends GfxFunction {
    public final static PreDefinedFunction DEFINITION = new PD_gfx_line();
    
    public PD_gfx_line(){
        super("gfx_line");
        addParameter("x0");
        addParameter("y0");
        addParameter("x1");
        addParameter("y1");
    }
    

    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException{
        StdDraw.line( doubleFromValue( args.get(0) ),
                      doubleFromValue( args.get(1) ),
                      doubleFromValue( args.get(2) ),
                      doubleFromValue( args.get(3) )
                    );    
        return SetlBoolean.TRUE;
    }
}
