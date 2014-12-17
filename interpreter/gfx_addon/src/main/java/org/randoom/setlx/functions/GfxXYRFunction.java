package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.*;
import org.randoom.setlx.types.*;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

public abstract class GfxXYRFunction extends GfxFunction{

    private final static ParameterDef X = createParameter("x");
    private final static ParameterDef Y = createParameter("y");
    private final static ParameterDef R = createParameter("r");

    protected GfxXYRFunction(){
        super();
        addParameter(X);
        addParameter(Y);
        addParameter(R);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) throws SetlException {
        final Double x = doubleFromValue(state, args.get(X));
        final Double y = doubleFromValue(state, args.get(Y));
        final Double r = doubleFromValue(state, args.get(R));
        executeStdDrawFunction(x, y, r);
        return SetlBoolean.TRUE;
    }

    protected abstract void executeStdDrawFunction(Double x, Double y, Double r);

}
