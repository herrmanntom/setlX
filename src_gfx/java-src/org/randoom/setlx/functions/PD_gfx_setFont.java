package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.StdDraw;

import java.awt.Font;
import java.util.List;

public class PD_gfx_setFont extends GfxFunction {

    public final static PreDefinedProcedure DEFINITION = new PD_gfx_setFont();

    public PD_gfx_setFont(){
        super();
        addParameter("fontName");
        allowFewerParameters();
    }

    @Override
    protected Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException{
        if ( args.size() == 1 ){
            final Font f = new Font( stringFromValue( state, args.get(0) ), StdDraw.getFont().getStyle(), StdDraw.getFont().getSize()  );
            StdDraw.setFont(f);
        }else {
            StdDraw.setFont();
        }
        return SetlBoolean.TRUE;
    }
}