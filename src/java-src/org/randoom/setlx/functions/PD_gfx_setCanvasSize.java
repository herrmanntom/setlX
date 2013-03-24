package org.randoom.setlx.functions;

import java.util.List;


import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_gfx_setCanvasSize extends GfxFunction {
    public final static PreDefinedFunction DEFINITION = new PD_gfx_setCanvasSize();
    
    private PD_gfx_setCanvasSize(){
        super("gfx_setCanvasSize");
        addParameter("w");
        addParameter("h");
        allowFewerParameters();
    }
    
    
    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException{
        if (args.size() == 2){
            StdDraw.setCanvasSize(integerFromValue(args.get(0)),integerFromValue(args.get(1)));
        }else{
            StdDraw.setCanvasSize();
        }
        return SetlBoolean.TRUE;
    }
}
