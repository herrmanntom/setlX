package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

import java.util.List;

public class PD_gfx_clear extends GfxFunction {
    public final static PreDefinedProcedure DEFINITION = new PD_gfx_clear();

    private PD_gfx_clear(){
        super();
    }

    @Override
    protected Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException{
        StdDraw.clear();
        return SetlBoolean.TRUE;
    }
}
