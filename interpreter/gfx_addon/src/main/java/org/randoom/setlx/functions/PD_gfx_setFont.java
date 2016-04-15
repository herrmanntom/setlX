package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.gfx.utilities.StdDraw;

import java.awt.Font;
import java.util.HashMap;

public class PD_gfx_setFont extends GfxFunction {

    private final static ParameterDefinition FONT       = createOptionalParameter("fontName", Om.OM);

    public  final static PreDefinedProcedure DEFINITION = new PD_gfx_setFont();

    public PD_gfx_setFont(){
        super();
        addParameter(FONT);
    }

    @Override
    protected Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException{
        if ( args.get(FONT) != Om.OM ){
            final Font f = new Font( stringFromValue( state, args.get(FONT) ), StdDraw.getFont().getStyle(), StdDraw.getFont().getSize()  );
            StdDraw.setFont(f);
        }else {
            StdDraw.setFont();
        }
        return SetlBoolean.TRUE;
    }
}