package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.gfx.utilities.StdDraw;

import java.util.HashMap;

public class PD_gfx_isKeyPressed extends GfxFunction {
    private final static ParameterDefinition KEY_CODE   = createParameter("keyCode");

    public  final static PreDefinedProcedure DEFINITION = new PD_gfx_isKeyPressed();

    private PD_gfx_isKeyPressed() {
        super();
        addParameter(KEY_CODE);
    }

    @Override
    protected Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        return SetlBoolean.valueOf( StdDraw.isKeyPressed( integerFromValue( state, args.get(KEY_CODE) ) ) );
    }

}
