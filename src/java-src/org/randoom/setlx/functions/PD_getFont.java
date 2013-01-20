package org.randoom.setlx.functions;

import java.util.List;


import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.StdDraw;

public class PD_getFont extends StdDrawFunction {
    public final static PreDefinedFunction DEFINITION = new PD_getFont();
    
    private PD_getFont(){
        super("getFont");
    }
    
    
    @Override
    protected Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException{
        return new SetlString( StdDraw.getFont().getFontName() );
    }
}
