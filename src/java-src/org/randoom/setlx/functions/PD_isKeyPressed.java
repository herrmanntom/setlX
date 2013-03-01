package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_isKeyPressed extends StdDrawFunction {
	public final static PreDefinedFunction DEFINITION = new PD_isKeyPressed();

    private PD_isKeyPressed() {
        super("isKeyPressed");
        addParameter("keyCode");
    }
	
	
	@Override
	protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
		return SetlBoolean.valueOf( StdDraw.isKeyPressed( integerFromValue( args.get(0) ) ) );
	}

}
