package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_gfx_addSpeedSlider extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_gfx_addSpeedSlider();

    protected PD_gfx_addSpeedSlider() {
        super();
        addParameter("add");
        allowFewerParameters();
    }

    @Override
    protected Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        if ( args.size() == 0 ){
            StdDraw.addSpeedSlider(true);
        }else{
            if ( args.get(0) instanceof SetlBoolean ){
                final SetlBoolean bool = (SetlBoolean) args.get(0);
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
