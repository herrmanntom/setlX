package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.SetlXUserPanel;
import org.randoom.setlx.utilities.State;

public class PD_gfx_addInputField extends GfxFunction {
public final static PreDefinedProcedure DEFINITION = new PD_gfx_addInputField();

    public PD_gfx_addInputField(){
        super();
        addParameter("prompt");
    }


    @Override
    protected Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException{
        SetlXUserPanel.getInstance().addInput( stringFromValue( args.get(0) ) );
        return SetlBoolean.TRUE;
    }
}
