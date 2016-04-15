package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.gfx.utilities.StdDraw;

import java.util.HashMap;

public class PD_gfx_filledPolygon extends GfxPolygonFunction {
    private final static ParameterDefinition X          = createParameter("x");
    private final static ParameterDefinition Y          = createParameter("y");

    public  final static PreDefinedProcedure DEFINITION = new PD_gfx_filledPolygon();

    public PD_gfx_filledPolygon(){
        super();
        addParameter(X);
        addParameter(Y);
    }


    @Override
    protected Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException{
        StdDraw.filledPolygon( doubleArrayFromValue(state, args.get(X)), doubleArrayFromValue(state, args.get(Y)));
        return SetlBoolean.TRUE;
    }
}