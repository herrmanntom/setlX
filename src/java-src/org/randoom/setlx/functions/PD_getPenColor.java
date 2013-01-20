package org.randoom.setlx.functions;

import java.util.List;


import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.StdDraw;

public class PD_getPenColor extends StdDrawFunction {
    public final static PreDefinedFunction DEFINITION = new PD_getPenColor();
    
    private PD_getPenColor(){
        super("getPenColor");
    }
    
    
    @Override
    protected Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException{
        return new SetlString( StdDraw.getPenColor().toString() );
    }
}