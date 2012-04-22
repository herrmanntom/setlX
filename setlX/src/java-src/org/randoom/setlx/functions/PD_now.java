package org.randoom.setlx.functions;

import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.Value;

import java.util.List;

// now()                   : get current time since epoch in ms

public class PD_now extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION
                                            = new PD_now();

    private PD_now() {
        super("now");
    }

    public Value execute(List<Value> args,
                         List<Value> writeBackVars
    ) {
        return new Rational("" + System.currentTimeMillis());
    }
}

