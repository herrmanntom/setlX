package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

import java.util.List;

public class PD_gfx_show extends GfxFunction {
    public final static PreDefinedProcedure DEFINITION = new PD_gfx_show();

    private PD_gfx_show(){
        super();
        addParameter("t");
        allowFewerParameters();
    }

    @Override
    protected Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException{
        if (args.isEmpty()){
            StdDraw.show();
        }else{
            StdDraw.show(integerFromValue(state, args.get(0)));
        }
        return SetlBoolean.TRUE;
    }
}
