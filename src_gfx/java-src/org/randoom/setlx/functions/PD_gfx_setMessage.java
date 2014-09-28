package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.SetlXUserPanel;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

public class PD_gfx_setMessage  extends GfxFunction {
    private final static ParameterDef        MESSAGE    = createParameter("message");

    public  final static PreDefinedProcedure DEFINITION = new PD_gfx_setMessage();

    public PD_gfx_setMessage(){
        super();
        addParameter(MESSAGE);
    }

    @Override
    protected Value execute(final State state, final HashMap<ParameterDef, Value> args) throws SetlException{
        SetlXUserPanel.getInstance().setMessage( stringFromValue( state, args.get(MESSAGE) ));
        return SetlBoolean.TRUE;
    }
}
