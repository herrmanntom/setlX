package org.randoom.setlx.functions;


import java.awt.Color;
import java.lang.reflect.Field;
import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_gfx_setPenColor extends GfxFunction {
    
    public final static PreDefinedFunction DEFINITION = new PD_gfx_setPenColor();
    
    public PD_gfx_setPenColor(){
        super("gfx_setPenColor");
        addParameter("r");
        addParameter("g");
        addParameter("b");
        allowFewerParameters();
    }
    

    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException{
        Color c = StdDraw.BLACK;
        if ( args.size() == 1 ){
            try {
                Field f = StdDraw.class.getField(args.get(0).getUnquotedString().toUpperCase());
                c = (Color) f.get(null);
                StdDraw.setPenColor(c);
            } catch (Exception e) {
                e.printStackTrace();
                return SetlBoolean.FALSE;
            }
        }else if ( args.size() == 3 ){
           if ( args.get(0).isInteger().equalTo(SetlBoolean.TRUE) ){
	           c = new Color( integerFromValue( args.get(0)), 
	                          integerFromValue( args.get(1)),
	                          integerFromValue( args.get(2))
	                        );
           }
           if ( args.get(0).isReal().equalTo(SetlBoolean.TRUE) ){
	           c = new Color( doubleFromValue( args.get(0)).floatValue(), 
	        		          doubleFromValue( args.get(1)).floatValue(),
	        		          doubleFromValue( args.get(2)).floatValue()
	                        );
           }
        }
        StdDraw.setPenColor(c);
        return SetlBoolean.TRUE;
    }
}
