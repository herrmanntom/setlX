package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.gfx.utilities.StdDraw;

import java.util.HashMap;

public class PD_gfx_nextKeyTyped extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_gfx_nextKeyTyped();

    private PD_gfx_nextKeyTyped() {
        super();
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
    	String result = "";
    	try {
    	    result += StdDraw.nextKeyTyped();
    	    return new SetlString( result );
    	} catch ( Exception ex ) {
            //no key typed throws Exception here
    		//just ignore this case and return false
    		return SetlBoolean.FALSE;
    	}
    }
}