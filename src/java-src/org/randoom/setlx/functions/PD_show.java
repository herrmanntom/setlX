package org.randoom.setlx.functions;

import java.util.List;


import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.StdDraw;

public class PD_show extends StdDrawFunction {
    public final static PreDefinedFunction DEFINITION = new PD_show();
    
    private PD_show(){
        super("show");
        addParameter("t");
        allowFewerParameters();
    }
    
    
    @Override
    protected Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException{
        if (args.isEmpty()){
            StdDraw.show();       
        }else{
            StdDraw.show(integerFromValue(args.get(0)));
        }
        return SetlBoolean.TRUE;
    }
}
