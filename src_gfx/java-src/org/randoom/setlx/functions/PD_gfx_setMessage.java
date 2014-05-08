package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.SetlXUserPanel;
import org.randoom.setlx.utilities.State;

import java.util.List;

public class PD_gfx_setMessage  extends GfxFunction {
    public final static PreDefinedProcedure DEFINITION = new PD_gfx_setMessage();

    public PD_gfx_setMessage(){
        super();
        addParameter("message");
    }

    @Override
    protected Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException{
        SetlXUserPanel.getInstance().setMessage( stringFromValue( state, args.get(0) ));
        return SetlBoolean.TRUE;
    }
}
