package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_gfx_hasNextKeyTyped extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_gfx_hasNextKeyTyped();

    private PD_gfx_hasNextKeyTyped() {
        super("hasNextKeyTyped");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
       return SetlBoolean.valueOf( StdDraw.hasNextKeyTyped() );
    }
}
