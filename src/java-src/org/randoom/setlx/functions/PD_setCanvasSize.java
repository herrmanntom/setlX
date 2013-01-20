package org.randoom.setlx.functions;

import java.util.List;


import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.StdDraw;

public class PD_setCanvasSize extends StdDrawFunction {
    public final static PreDefinedFunction DEFINITION = new PD_setCanvasSize();
    
    private PD_setCanvasSize(){
        super("setCanvasSize");
        addParameter("w");
        addParameter("h");
        allowFewerParameters();
    }
    
    
    @Override
    protected Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException{
        if (args.size() == 2){
            StdDraw.setCanvasSize(integerFromValue(args.get(0)),integerFromValue(args.get(1)));
        }else{
            StdDraw.setCanvasSize();
        }
        return SetlBoolean.TRUE;
    }
}
