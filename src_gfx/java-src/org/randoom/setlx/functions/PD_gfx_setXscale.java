package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

import java.util.List;

public class PD_gfx_setXscale extends GfxFunction {
    public final static PreDefinedProcedure DEFINITION = new PD_gfx_setXscale();

    private PD_gfx_setXscale(){
        super();
        addParameter("min");
        addParameter("max");
        setMinimumNumberOfParameters(0);
    }


    @Override
    protected Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException{
        if (args.size()==2){
            StdDraw.setXscale(doubleFromValue(state, args.get(0)),doubleFromValue(state, args.get(1)));
        }else{
            StdDraw.setXscale();
        }
        return SetlBoolean.TRUE;
    }
}
