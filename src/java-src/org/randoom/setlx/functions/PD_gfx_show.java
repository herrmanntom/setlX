package org.randoom.setlx.functions;

import java.util.List;


import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_gfx_show extends GfxFunction {
    public final static PreDefinedFunction DEFINITION = new PD_gfx_show();
    
    private PD_gfx_show(){
        super("gfx_show");
        addParameter("t");
        allowFewerParameters();
    }
    
    
    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException{
        if (args.isEmpty()){
            StdDraw.show();       
        }else{
            StdDraw.show(integerFromValue(args.get(0)));
        }
        return SetlBoolean.TRUE;
    }
}
