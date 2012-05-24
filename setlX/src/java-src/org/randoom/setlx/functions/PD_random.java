package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Real;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;

import java.util.List;

// random()                : get random Real between 0.0 and 1.0 (inclusive)

public class PD_random extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_random();

    private PD_random() {
        super("random");
    }

    public Real execute(List<Value> args, List<Value> writeBackVars) {
        return new Real(Environment.getRandomDouble());
    }
}

