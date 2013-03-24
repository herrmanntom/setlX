package org.randoom.setlx.functions;


import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_gfx_arc extends GfxFunction{
    public final static PreDefinedProcedure DEFINITION = new PD_gfx_arc();

    private PD_gfx_arc() {
        super();
        addParameter("x");
        addParameter("y");
        addParameter("r");
        addParameter("angle1");
        addParameter("angle2");
    }


    @Override
    protected Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException{
        StdDraw.arc( doubleFromValue(state, args.get(0)),
                     doubleFromValue(state, args.get(1)),
                     doubleFromValue(state, args.get(2)),
                     doubleFromValue(state, args.get(3)),
                     doubleFromValue(state, args.get(4))
                   );
        return SetlBoolean.TRUE;
    }

}
