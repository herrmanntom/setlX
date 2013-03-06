package org.randoom.setlx.functions;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Field;
import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_gfx_setFont extends GfxFunction {
    
    public final static PreDefinedFunction DEFINITION = new PD_gfx_setFont();
    
    public PD_gfx_setFont(){
        super("gfx_setFont");
        addParameter("fontName");
        allowFewerParameters();
    }
    

    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException{
        if ( args.size() == 1 ){
            Font f = new Font( stringFromValue( args.get(0) ), StdDraw.getFont().getStyle(), StdDraw.getFont().getSize()  );
            StdDraw.setFont(f);
        }else {
        	StdDraw.setFont();
        }
        return SetlBoolean.TRUE;
    }
}