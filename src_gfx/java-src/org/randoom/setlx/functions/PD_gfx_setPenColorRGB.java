package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

import java.awt.Color;
import java.util.List;

public class PD_gfx_setPenColorRGB extends GfxFunction {

    public final static PreDefinedProcedure DEFINITION = new PD_gfx_setPenColorRGB();

    public PD_gfx_setPenColorRGB(){
        super();
        addParameter("r");
        addParameter("g");
        addParameter("b");
    }

    private boolean checkParameter( final double rgb ){
        return rgb >= 0.0 && rgb <= 1.0;
    }

    @Override
    protected Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException{
        final double r = doubleFromValue( state, args.get(0) );
        final double g = doubleFromValue( state, args.get(1) );
        final double b = doubleFromValue( state, args.get(2) );
        if ( checkParameter(r) && checkParameter(g) && checkParameter(b) ){
            StdDraw.setPenColor(new Color(new Float(r),new Float(g),new Float(b)));
            return SetlBoolean.TRUE;
        }else{
            return SetlBoolean.FALSE;
        }
    }
}