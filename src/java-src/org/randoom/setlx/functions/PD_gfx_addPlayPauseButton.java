package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.functions.PD_gfx_addSpeedSlider;
import org.randoom.setlx.functions.PreDefinedFunction;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_gfx_addPlayPauseButton extends PreDefinedFunction {

    public final static PreDefinedFunction DEFINITION = new PD_gfx_addPlayPauseButton();
    
    protected PD_gfx_addPlayPauseButton() {
        super("PD_addPlayPauseButton");
        addParameter("add");
        allowFewerParameters();
    }

    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
        if ( args.size() == 0 ){
            StdDraw.addPlayPauseButton(true);
        }else{
            if ( args.get(0) instanceof SetlBoolean ){
                SetlBoolean bool = (SetlBoolean) args.get(0);
                if ( bool.equalTo(SetlBoolean.TRUE) ){
                    StdDraw.addPlayPauseButton(true);
                }else{
                    StdDraw.addPlayPauseButton(false);
                }
            }
        }
        return SetlBoolean.TRUE;
    }

}