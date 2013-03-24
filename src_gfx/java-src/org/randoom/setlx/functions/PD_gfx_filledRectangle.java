package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

import java.util.List;

public class PD_gfx_filledRectangle extends GfxFunction {

    public final static PreDefinedProcedure DEFINITION = new PD_gfx_filledRectangle();

    public PD_gfx_filledRectangle(){
        super();
        addParameter("x");
        addParameter("y");
        addParameter("halfWidth");
        addParameter("halfHeight");
    }


    @Override
    protected Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException{
        StdDraw.filledRectangle( doubleFromValue( state, args.get(0) ),
                                 doubleFromValue( state, args.get(1) ),
                                 doubleFromValue( state, args.get(2) ),
                                 doubleFromValue( state, args.get(3) )
                               );
        return SetlBoolean.TRUE;
    }
}
