package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.gfx.utilities.StdDraw;

import java.awt.Color;
import java.util.HashMap;

public class PD_gfx_setPenColorRGB extends GfxFunction {
    private final static ParameterDefinition R          = createParameter("r");
    private final static ParameterDefinition G          = createParameter("g");
    private final static ParameterDefinition B          = createParameter("b");

    public  final static PreDefinedProcedure DEFINITION = new PD_gfx_setPenColorRGB();

    public PD_gfx_setPenColorRGB(){
        super();
        addParameter(R);
        addParameter(G);
        addParameter(B);
    }

    private boolean checkParameter( final double rgb ){
        return rgb >= 0.0 && rgb <= 1.0;
    }

    @Override
    protected Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException{
        final double r = doubleFromValue( state, args.get(R) );
        final double g = doubleFromValue( state, args.get(G) );
        final double b = doubleFromValue( state, args.get(B) );
        if ( checkParameter(r) && checkParameter(g) && checkParameter(b) ){
            StdDraw.setPenColor(new Color(new Float(r),new Float(g),new Float(b)));
            return SetlBoolean.TRUE;
        }else{
            return SetlBoolean.FALSE;
        }
    }
}