package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

import java.util.List;

public class PD_gfx_isKeyPressed extends GfxFunction {
    public final static PreDefinedProcedure DEFINITION = new PD_gfx_isKeyPressed();

    private PD_gfx_isKeyPressed() {
        super();
        addParameter("keyCode");
    }

    @Override
    protected Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        return SetlBoolean.valueOf( StdDraw.isKeyPressed( integerFromValue( state, args.get(0) ) ) );
    }

}
