package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Real;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.StdDraw;


public class PD_getPenRadius extends StdDrawFunction {
    public final static PreDefinedFunction DEFINITION = new PD_getPenRadius();
    
    private PD_getPenRadius(){
        super("getPenRadius");
    }
    
    
    @Override
    protected Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException{
        return new Real(StdDraw.getPenRadius());
    }


   
}