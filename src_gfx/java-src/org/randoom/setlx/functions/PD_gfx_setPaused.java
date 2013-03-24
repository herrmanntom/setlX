package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

import java.util.List;

public class PD_gfx_setPaused extends PreDefinedProcedure {

    public final static PreDefinedProcedure DEFINITION = new PD_gfx_setPaused();

    protected PD_gfx_setPaused() {
        super();
        addParameter("paused");
    }

    @Override
    protected Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        SetlBoolean bool;
        if ( args.get(0) instanceof SetlBoolean ){
            bool = (SetlBoolean) args.get(0);
            if ( bool.equalTo(SetlBoolean.TRUE) ){
                StdDraw.setPaused(true);
            }else{
                StdDraw.setPaused(false);
            }
        }else{
            return SetlBoolean.FALSE;
        }
        return bool;
    }

}
