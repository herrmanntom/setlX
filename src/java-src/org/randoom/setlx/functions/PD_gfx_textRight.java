package org.randoom.setlx.functions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

public class PD_gfx_textRight extends GfxFunction {
    public final static PreDefinedFunction DEFINITION = new PD_gfx_textRight();
    
    
    protected PD_gfx_textRight() {
        super("gfx_textRight");
        addParameter("x");
        addParameter("y");
        addParameter("string");
    }

    @Override
    protected Value execute(State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
        double x = doubleFromValue( args.get(0) );
        double y = doubleFromValue( args.get(1) );
        String s = stringFromValue( args.get(2) );
        StdDraw.textRight(x, y, s);
        return SetlBoolean.TRUE;
    }

}
