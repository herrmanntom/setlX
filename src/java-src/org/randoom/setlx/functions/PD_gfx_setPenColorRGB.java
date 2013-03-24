package org.randoom.setlx.functions;


import java.awt.Color;
import java.lang.reflect.Field;
import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_gfx_setPenColorRGB extends GfxFunction {
    
    public final static PreDefinedFunction DEFINITION = new PD_gfx_setPenColorRGB();
    
    public PD_gfx_setPenColorRGB(){
        super("gfx_setPenColorRGB");
        addParameter("r");
        addParameter("g");
        addParameter("b");
    }
    

    private boolean checkParameter( double rgb ){
    	return rgb >= 0.0 && rgb <= 1.0;
    }
    
    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException{
        double r = doubleFromValue( args.get(0) );
        double g = doubleFromValue( args.get(1) );
        double b = doubleFromValue( args.get(2) );
        if ( checkParameter(r) && checkParameter(g) && checkParameter(b) ){
            StdDraw.setPenColor(new Color(new Float(r),new Float(g),new Float(b)));
            return SetlBoolean.TRUE;
        }else{
        	return SetlBoolean.FALSE;
        }
    }
}