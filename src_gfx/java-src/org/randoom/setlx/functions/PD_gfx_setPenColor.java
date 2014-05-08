package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.List;

public class PD_gfx_setPenColor extends GfxFunction {

    public final static PreDefinedProcedure DEFINITION = new PD_gfx_setPenColor();

    public PD_gfx_setPenColor(){
        super();
        addParameter("color");
        setMinimumNumberOfParameters(0);
    }

    @Override
    protected Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException{
        if ( args.size() == 0 ) {
        	StdDraw.setPenColor();
        } else {
	    	Color c = StdDraw.BLACK;
	        try {
	            final Field f = StdDraw.class.getField(args.get(0).getUnquotedString(state).toUpperCase());
	            c = (Color) f.get(null);
	            StdDraw.setPenColor(c);
	        } catch (final Exception e) {
	            final ByteArrayOutputStream out = new ByteArrayOutputStream();
	            e.printStackTrace(new PrintStream(out));
	            state.errWrite(out.toString());
	            return SetlBoolean.FALSE;
	        }
        }
        return SetlBoolean.TRUE;
    }
}
