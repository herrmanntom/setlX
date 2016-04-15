package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.gfx.utilities.StdDraw;

import java.util.HashMap;

public class PD_gfx_setScale extends GfxFunction {
    private final static ParameterDefinition MIN        = createParameter("min");
    private final static ParameterDefinition MAX        = createParameter("max");

    public  final static PreDefinedProcedure DEFINITION = new PD_gfx_setScale();

    private PD_gfx_setScale(){
        super();
        addParameter(MIN);
        addParameter(MAX);
    }

    @Override
    protected Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException{
        StdDraw.setScale(doubleFromValue(state, args.get(MIN)),doubleFromValue(state, args.get(MAX)));
        return SetlBoolean.TRUE;
    }
}
