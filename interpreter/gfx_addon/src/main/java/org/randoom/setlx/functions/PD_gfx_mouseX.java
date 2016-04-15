package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.gfx.utilities.StdDraw;

import java.util.HashMap;

public class PD_gfx_mouseX extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_gfx_mouseX();

    private PD_gfx_mouseX(){
        super();
    }

    @Override
    protected Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException{
        return SetlDouble.valueOf(StdDraw.mouseX());
    }
}
