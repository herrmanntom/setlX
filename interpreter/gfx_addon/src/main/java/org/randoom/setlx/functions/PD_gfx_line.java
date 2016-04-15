package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.gfx.utilities.StdDraw;

import java.util.HashMap;

public class PD_gfx_line extends GfxFunction {
    private final static ParameterDefinition X0         = createParameter("x0");
    private final static ParameterDefinition Y0         = createParameter("y0");
    private final static ParameterDefinition X1         = createParameter("x1");
    private final static ParameterDefinition Y1         = createParameter("y1");

    public  final static PreDefinedProcedure DEFINITION = new PD_gfx_line();

    public PD_gfx_line(){
        super();
        addParameter(X0);
        addParameter(Y0);
        addParameter(X1);
        addParameter(Y1);
    }

    @Override
    protected Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException{
        StdDraw.line( doubleFromValue( state, args.get(X0) ),
                      doubleFromValue( state, args.get(Y0) ),
                      doubleFromValue( state, args.get(X1) ),
                      doubleFromValue( state, args.get(Y1) )
                    );
        return SetlBoolean.TRUE;
    }
}
