package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.gfx.utilities.StdDraw;

import java.util.HashMap;

public class PD_gfx_show extends GfxFunction {
    private final static ParameterDefinition T          = createOptionalParameter("t", Om.OM);

    public  final static PreDefinedProcedure DEFINITION = new PD_gfx_show();

    private PD_gfx_show(){
        super();
        addParameter(T);
    }

    @Override
    protected Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException{
        if (args.get(T) == Om.OM){
            StdDraw.show();
        }else{
            StdDraw.show(integerFromValue(state, args.get(T)));
        }
        return SetlBoolean.TRUE;
    }
}
