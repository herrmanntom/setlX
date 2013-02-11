package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_addSpeedSlider extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_addSpeedSlider();
    
    protected PD_addSpeedSlider() {
        super("addSpeedSlider");
        addParameter("add");
        allowFewerParameters();
    }

    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
        if ( args.size() == 0 ){
            StdDraw.addSpeedSlider(true);
        }else{
            if ( args.get(0) instanceof SetlBoolean ){
                SetlBoolean bool = (SetlBoolean) args.get(0);
                if ( bool.equalTo(SetlBoolean.TRUE) ){
                    StdDraw.addSpeedSlider(true);
                }else{
                    StdDraw.addSpeedSlider(false);
                }
            }
        }
        return SetlBoolean.TRUE;
    }

}
