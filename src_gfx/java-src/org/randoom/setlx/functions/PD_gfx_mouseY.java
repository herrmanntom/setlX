package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_gfx_mouseY extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_gfx_mouseY();

    private PD_gfx_mouseY(){
        super();
    }

    @Override
    protected Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException{
        return SetlDouble.valueOf(StdDraw.mouseY());
    }
}
