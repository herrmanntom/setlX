package org.randoom.setlx.functions;

import java.util.List;


import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_clear extends StdDrawFunction {
    public final static PreDefinedFunction DEFINITION = new PD_clear();
    
    private PD_clear(){
        super("clear");
        addParameter("color");
        allowFewerParameters();
    }
    
    
    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException{
        if (args.isEmpty()){
            StdDraw.clear();       
        }else{
            StdDraw.clear();
        }
        return SetlBoolean.TRUE;
    }
}
