package org.randoom.setlx.functions;

import java.util.List;


import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_gfx_setScale extends GfxFunction {
    public final static PreDefinedFunction DEFINITION = new PD_gfx_setScale();
    
    private PD_gfx_setScale(){
        super("gfx_setScale");
        addParameter("min");
        addParameter("max");
    }
    
    
    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException{
        StdDraw.setScale(doubleFromValue(args.get(0)),doubleFromValue(args.get(1)));
        return SetlBoolean.TRUE;
    }
}
