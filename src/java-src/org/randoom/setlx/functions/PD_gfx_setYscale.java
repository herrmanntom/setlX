package org.randoom.setlx.functions;

import java.util.List;


import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_gfx_setYscale extends GfxFunction {
    public final static PreDefinedFunction DEFINITION = new PD_gfx_setYscale();
    
    private PD_gfx_setYscale(){
        super("gfx_setYscale");
        addParameter("min");
        addParameter("max");
        allowFewerParameters();
    }
    
    
    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException{
        if (args.size()==2){
            StdDraw.setYscale(doubleFromValue(args.get(0)),doubleFromValue(args.get(1)));
        }else{
            StdDraw.setYscale();
        }
        return SetlBoolean.TRUE;
    }
}
