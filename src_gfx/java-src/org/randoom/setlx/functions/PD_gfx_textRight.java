package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

import java.util.List;

public class PD_gfx_textRight extends GfxFunction {
    public final static PreDefinedProcedure DEFINITION = new PD_gfx_textRight();

    protected PD_gfx_textRight() {
        super();
        addParameter("x");
        addParameter("y");
        addParameter("string");
    }

    @Override
    protected Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        final double x = doubleFromValue( state, args.get(0) );
        final double y = doubleFromValue( state, args.get(1) );
        final String s = stringFromValue( args.get(2) );
        StdDraw.textRight(x, y, s);
        return SetlBoolean.TRUE;
    }

}
