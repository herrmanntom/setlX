package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.gfx.utilities.StdDraw;

import java.util.HashMap;

public class PD_gfx_setPenRadius extends GfxFunction {
    private final static ParameterDefinition R          = createOptionalParameter("r", Om.OM);

    public  final static PreDefinedProcedure DEFINITION = new PD_gfx_setPenRadius();

    private PD_gfx_setPenRadius(){
        super();
        addParameter(R);
    }


    @Override
    protected Value execute( final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException{
        if (args.get(R) != Om.OM){
            StdDraw.setPenRadius(doubleFromValue(state, args.get(R)));
        }else{
            StdDraw.setPenRadius();
        }
        return SetlBoolean.TRUE;
    }
}