package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.gfx.utilities.StdDraw;

import java.util.HashMap;

public class PD_gfx_filledRectangle extends GfxFunction {
    private final static ParameterDefinition X          = createParameter("x");
    private final static ParameterDefinition Y          = createParameter("y");
    private final static ParameterDefinition HALF_W     = createParameter("halfWidth");
    private final static ParameterDefinition HALF_H     = createParameter("halfHeight");

    public final static PreDefinedProcedure DEFINITION = new PD_gfx_filledRectangle();

    public PD_gfx_filledRectangle(){
        super();
        addParameter(X);
        addParameter(Y);
        addParameter(HALF_W);
        addParameter(HALF_H);
    }


    @Override
    protected Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException{
        StdDraw.filledRectangle( doubleFromValue( state, args.get(X) ),
                                 doubleFromValue( state, args.get(Y) ),
                                 doubleFromValue( state, args.get(HALF_W) ),
                                 doubleFromValue( state, args.get(HALF_H) )
                               );
        return SetlBoolean.TRUE;
    }
}
