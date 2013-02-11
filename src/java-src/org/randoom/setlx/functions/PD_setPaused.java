package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.functions.PD_addSpeedSlider;
import org.randoom.setlx.functions.PreDefinedFunction;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_setPaused extends PreDefinedFunction {

    public final static PreDefinedFunction DEFINITION = new PD_setPaused();
    
    protected PD_setPaused() {
        super("setPaused");
        addParameter("paused");
    }

    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
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
