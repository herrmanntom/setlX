package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_setPenRadius extends StdDrawFunction {
    public final static PreDefinedFunction DEFINITION = new PD_setPenRadius();
    
    private PD_setPenRadius(){
        super("setPenRadius");
        addParameter("r");
        allowFewerParameters();
    }
    
    
    @Override
    protected Value execute( State state, List<Value> args, List<Value> writeBackVars) throws SetlException{
        if (!args.isEmpty()){
            StdDraw.setPenRadius(doubleFromValue(args.get(0)));
        }else{
            StdDraw.setPenRadius();
        }
        return SetlBoolean.TRUE;
    }
}