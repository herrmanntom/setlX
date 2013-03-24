package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.functions.PD_gfx_addSpeedSlider;
import org.randoom.setlx.functions.PreDefinedFunction;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_gfx_isPaused extends PreDefinedFunction {

    public final static PreDefinedFunction DEFINITION = new PD_gfx_isPaused();
    
    protected PD_gfx_isPaused() {
        super("gfx_isPaused");
    }

    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
        return SetlBoolean.valueOf(StdDraw.isPaused());
    }

}
