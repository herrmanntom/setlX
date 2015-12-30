package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.gfx.utilities.StdDraw;

import java.util.HashMap;

public class PD_gfx_ellipse extends GfxFunction {
    private final static ParameterDef        X          = createParameter("x");
    private final static ParameterDef        Y          = createParameter("y");
    private final static ParameterDef        SEMI_1     = createParameter("semiMajorAxis");
    private final static ParameterDef        SEMI_2     = createParameter("semiMinorAxis");

    public  final static PreDefinedProcedure DEFINITION = new PD_gfx_ellipse();

    public PD_gfx_ellipse(){
        super();
        addParameter(X);
        addParameter(Y);
        addParameter(SEMI_1);
        addParameter(SEMI_2);
    }


    @Override
    protected Value execute(final State state, final HashMap<ParameterDef, Value> args) throws SetlException{
        StdDraw.ellipse(   doubleFromValue( state, args.get(X) ),
                           doubleFromValue( state, args.get(Y) ),
                           doubleFromValue( state, args.get(SEMI_1) ),
                           doubleFromValue( state, args.get(SEMI_2) )
                         );
        return SetlBoolean.TRUE;
    }
}