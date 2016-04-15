package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.gfx.utilities.StdDraw;

import java.util.HashMap;

public class PD_gfx_addSpeedSlider extends PreDefinedProcedure {

    private final static ParameterDefinition ADD        = createOptionalParameter("add", Om.OM);

    public  final static PreDefinedProcedure DEFINITION = new PD_gfx_addSpeedSlider();

    protected PD_gfx_addSpeedSlider() {
        super();
        addParameter(ADD);
    }

    @Override
    protected Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        if ( args.get(ADD)  == Om.OM ){
            StdDraw.addSpeedSlider(true);
        }else{
            if ( args.get(ADD) instanceof SetlBoolean ){
                final SetlBoolean bool = (SetlBoolean) args.get(ADD);
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
