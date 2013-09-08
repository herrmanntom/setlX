package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_gfx_picture extends GfxFunction {

	
	public final static PreDefinedProcedure DEFINITION = new PD_gfx_picture();

    public PD_gfx_picture(){
        super();
        addParameter("x");
        addParameter("y");
        addParameter("picture");
        addParameter("w");
        addParameter("h");
        allowFewerParameters();
    }
	
	@Override
	protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
		try {
			switch( args.size() ) {
			case 3:
				StdDraw.picture( doubleFromValue(state,args.get(0)), 
						         doubleFromValue(state,args.get(1)), 
						         stringFromValue(args.get(2)) );
				break;
			case 5:
				StdDraw.picture( doubleFromValue(state,args.get(0)), 
						         doubleFromValue(state,args.get(1)), 
						         stringFromValue(args.get(2)), 
						         doubleFromValue(state,args.get(3)), 
						         doubleFromValue(state,args.get(4)) 
						       );
				break;
		    default:
		    	return SetlBoolean.FALSE;
			}
		} catch ( Exception ex ) {
		//some error in StdDraw
	        return SetlBoolean.FALSE;
		}
		return SetlBoolean.TRUE;
	}

}
