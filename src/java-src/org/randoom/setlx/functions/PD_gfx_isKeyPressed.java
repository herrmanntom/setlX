package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_gfx_isKeyPressed extends GfxFunction {
	public final static PreDefinedFunction DEFINITION = new PD_gfx_isKeyPressed();

    private PD_gfx_isKeyPressed() {
        super("gfx_isKeyPressed");
        addParameter("keyCode");
    }
	
	
	@Override
	protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
		return SetlBoolean.valueOf( StdDraw.isKeyPressed( integerFromValue( args.get(0) ) ) );
	}

}
