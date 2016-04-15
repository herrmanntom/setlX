package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.gfx.utilities.StdDraw;

import java.util.HashMap;

public class PD_gfx_text extends GfxFunction {
    private final static ParameterDefinition X          = createParameter("x");
    private final static ParameterDefinition Y          = createParameter("y");
    private final static ParameterDefinition STRING     = createParameter("string");
    private final static ParameterDefinition DEGREES    = createOptionalParameter("degrees", SetlDouble.ZERO);

    public  final static PreDefinedProcedure DEFINITION = new PD_gfx_text();

    protected PD_gfx_text() {
        super();
        addParameter(X);
        addParameter(Y);
        addParameter(STRING);
        addParameter(DEGREES);
    }

    @Override
    protected Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        final double x = doubleFromValue(state, args.get(X));
        final double y = doubleFromValue(state, args.get(Y));
        final String s = stringFromValue(state, args.get(STRING));
        StdDraw.text(x, y, s, doubleFromValue(state, args.get(DEGREES)));
        return SetlBoolean.TRUE;
    }

}
