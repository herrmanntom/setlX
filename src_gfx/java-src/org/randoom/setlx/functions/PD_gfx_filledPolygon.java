package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

import java.util.List;

public class PD_gfx_filledPolygon extends GfxPolygonFunction {
    public final static PreDefinedProcedure DEFINITION = new PD_gfx_filledPolygon();

    public PD_gfx_filledPolygon(){
        super();
        addParameter("x");
        addParameter("y");
    }


    @Override
    protected Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException{
        StdDraw.filledPolygon( doubleArrayFromValue(state, args.get(0)), doubleArrayFromValue(state, args.get(1)));
        return SetlBoolean.TRUE;
    }
}