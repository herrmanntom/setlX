package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.gfx.utilities.StdDraw;

import java.util.HashMap;

public class PD_gfx_picture extends GfxFunction {
    private final static ParameterDefinition X          = createParameter("x");
    private final static ParameterDefinition Y          = createParameter("y");
    private final static ParameterDefinition PICTURE    = createParameter("picture");
    private final static ParameterDefinition W          = createOptionalParameter("w", Om.OM);
    private final static ParameterDefinition H          = createOptionalParameter("h", Om.OM);


    public  final static PreDefinedProcedure DEFINITION = new PD_gfx_picture();

    public PD_gfx_picture(){
        super();
        addParameter(X);
        addParameter(Y);
        addParameter(PICTURE);
        addParameter(W);
        addParameter(H);
    }

	@Override
	protected Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
		try {
            if (args.get(W) != Om.OM && args.get(H) != Om.OM) {
                StdDraw.picture(
                        doubleFromValue(state,args.get(X)),
                        doubleFromValue(state,args.get(Y)),
                        stringFromValue(state,args.get(PICTURE)),
                        doubleFromValue(state,args.get(W)),
                        doubleFromValue(state,args.get(H))
                );
            } else if (args.get(W) == Om.OM && args.get(H) == Om.OM) {
                StdDraw.picture(
                        doubleFromValue(state,args.get(X)),
                        doubleFromValue(state,args.get(Y)),
                        stringFromValue(state,args.get(PICTURE))
                );
            } else {
		    	return SetlBoolean.FALSE;
			}
		} catch ( final Exception ex ) {
		//some error in StdDraw
	        return SetlBoolean.FALSE;
		}
		return SetlBoolean.TRUE;
	}

}
