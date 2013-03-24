package org.randoom.setlx.functions;

import java.util.List;
import org.randoom.setlx.exceptions.*;
import org.randoom.setlx.types.*;
import org.randoom.setlx.utilities.State;

public abstract class GfxXYRFunction extends GfxFunction{

    protected GfxXYRFunction(){
        super();
        addParameter("x");
        addParameter("y");
        addParameter("r");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        final Double x = doubleFromValue(state, args.get(0));
        final Double y = doubleFromValue(state, args.get(1));
        final Double r = doubleFromValue(state, args.get(2));
        executeStdDrawFunction(x, y, r);
        return SetlBoolean.TRUE;
    }

    protected abstract void executeStdDrawFunction(Double x, Double y, Double r);

}
