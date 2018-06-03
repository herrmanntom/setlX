package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.gfx.utilities.StdDraw;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.HashMap;

public class PD_gfx_setPenColor extends GfxFunction {
    private final static ParameterDefinition COLOR      = createOptionalParameter("color", Om.OM);

    public  final static PreDefinedProcedure DEFINITION = new PD_gfx_setPenColor();

    public PD_gfx_setPenColor(){
        super();
        addParameter(COLOR);
    }

    @Override
    protected Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException{
        if ( args.get(COLOR) == Om.OM ) {
        	StdDraw.setPenColor();
        } else {
	        try {
	            final Field f = StdDraw.class.getField(args.get(COLOR).getUnquotedString(state).toUpperCase());
                Color c = (Color) f.get(null);
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
