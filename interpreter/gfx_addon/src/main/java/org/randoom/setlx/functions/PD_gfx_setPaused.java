package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.gfx.utilities.StdDraw;

import java.util.HashMap;

public class PD_gfx_setPaused extends PreDefinedProcedure {
    private final static ParameterDefinition PAUSED     = createParameter("paused");

    public  final static PreDefinedProcedure DEFINITION = new PD_gfx_setPaused();

    protected PD_gfx_setPaused() {
        super();
        addParameter(PAUSED);
    }

    @Override
    protected Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        SetlBoolean bool;
        if ( args.get(PAUSED) instanceof SetlBoolean ){
            bool = (SetlBoolean) args.get(PAUSED);
            if ( bool.equalTo(SetlBoolean.TRUE) ){
                StdDraw.setPaused(true);
            } else {
                StdDraw.setPaused(false);
            }
        }else{
            return SetlBoolean.FALSE;
        }
        return bool;
    }

}
