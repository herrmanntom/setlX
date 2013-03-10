package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.ThrownInSetlXException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// throw(value)            : stops execution and throws value to be catched by try-catch block

public class PD_throw extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_throw();

    private PD_throw() {
        super();
        addParameter("value");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws ThrownInSetlXException {
        throw new ThrownInSetlXException(args.get(0));
    }
}

