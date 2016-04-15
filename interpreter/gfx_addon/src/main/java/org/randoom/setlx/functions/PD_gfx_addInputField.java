package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.gfx.utilities.SetlXUserPanel;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

public class PD_gfx_addInputField extends GfxFunction {
    private final static ParameterDefinition PROMPT     = createParameter("prompt");

    public  final static PreDefinedProcedure DEFINITION = new PD_gfx_addInputField();

    public PD_gfx_addInputField(){
        super();
        addParameter(PROMPT);
    }


    @Override
    protected Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException{
        SetlXUserPanel.getInstance().addInput( stringFromValue( state, args.get(PROMPT) ) );
        return SetlBoolean.TRUE;
    }
}
