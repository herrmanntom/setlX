package org.randoom.setlx.functions;

import java.util.List;


import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.StdDraw;

public class PD_setXscale extends StdDrawFunction {
    public final static PreDefinedFunction DEFINITION = new PD_setXscale();
    
    private PD_setXscale(){
        super("setXscale");
        addParameter("min");
        addParameter("max");
        allowFewerParameters();
    }
    
    
    @Override
    protected Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException{
        if (args.size()==2){
            StdDraw.setXscale(doubleFromValue(args.get(0)),doubleFromValue(args.get(1)));
        }else{
            StdDraw.setXscale();
        }
        return SetlBoolean.TRUE;
    }
}
