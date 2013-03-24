package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.SetlXUserPanel;
import org.randoom.setlx.utilities.State;

public class PD_gfx_setMessage  extends GfxFunction {
    public final static PreDefinedFunction DEFINITION = new PD_gfx_setMessage();
    
    public PD_gfx_setMessage(){
        super("gfx_setMessage");
        addParameter("message");
    }
    

    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException{
        SetlXUserPanel.getInstance().setMessage( stringFromValue( args.get(0) )); 
        return SetlBoolean.TRUE;
    }
}
