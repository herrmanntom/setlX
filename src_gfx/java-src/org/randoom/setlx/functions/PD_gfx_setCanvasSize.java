package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

import java.util.List;

public class PD_gfx_setCanvasSize extends GfxFunction {
    public final static PreDefinedProcedure DEFINITION = new PD_gfx_setCanvasSize();

    private PD_gfx_setCanvasSize(){
        super();
        addParameter("w");
        addParameter("h");
        setMinimumNumberOfParameters(0);
    }

    @Override
    protected Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException{
        if (args.size() == 2){
            StdDraw.setCanvasSize(integerFromValue(state, args.get(0)),integerFromValue(state, args.get(1)));
        }else{
            StdDraw.setCanvasSize();
        }
        return SetlBoolean.TRUE;
    }
}
