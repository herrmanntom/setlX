package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.*;
import org.randoom.setlx.types.*;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

public abstract class GfxXYRFunction extends GfxFunction{

    private final static ParameterDefinition X = createParameter("x");
    private final static ParameterDefinition Y = createParameter("y");
    private final static ParameterDefinition R = createParameter("r");

    protected GfxXYRFunction(){
        super();
        addParameter(X);
        addParameter(Y);
        addParameter(R);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Double x = doubleFromValue(state, args.get(X));
        final Double y = doubleFromValue(state, args.get(Y));
        final Double r = doubleFromValue(state, args.get(R));
        executeStdDrawFunction(x, y, r);
        return SetlBoolean.TRUE;
    }

    protected abstract void executeStdDrawFunction(Double x, Double y, Double r);

}
