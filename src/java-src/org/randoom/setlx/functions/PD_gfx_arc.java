package org.randoom.setlx.functions;


import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

//square(NumberValue,NumberValue,NumberValue) : 
//
public class PD_gfx_arc extends GfxFunction{
    public final static PreDefinedFunction DEFINITION = new PD_gfx_arc();

    private PD_gfx_arc() {
        super("gfx_arc");
        addParameter("x");
        addParameter("y");
        addParameter("r");
        addParameter("angle1");
        addParameter("angle2");
    }
    

    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException{
        StdDraw.arc( doubleFromValue(args.get(0)),
                     doubleFromValue(args.get(1)),
                     doubleFromValue(args.get(2)),
                     doubleFromValue(args.get(3)),
                     doubleFromValue(args.get(4)) 
                   );
        return SetlBoolean.TRUE; 
    }
    
}
