package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.SetlXUserPanel;
import org.randoom.setlx.utilities.State;

public class PD_gfx_getInput extends GfxFunction {
public final static PreDefinedFunction DEFINITION = new PD_gfx_getInput();
    
    public PD_gfx_getInput(){
        super("gfx_getInput");
    }
    

    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException{
        String result = SetlXUserPanel.getInstance().getInput();
        return new SetlString(result);
    }
}
