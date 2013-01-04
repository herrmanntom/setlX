package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.Real;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.State;

import java.util.List;

// random([real])                : Get random Real between 0.0 and argument (inclusive).
//                                 If no argument is used, 1.0 is implied.

public class PD_random extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_random();

    private PD_random() {
        super("random");
        addParameter("numberOfChoices");
        allowFewerParameters();
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) throws SetlException {
        Value random = Real.valueOf(Environment.getRandomDouble());
        if (args.size() == 1) {
            Value arg = args.get(0);
            if (arg.equalTo(Rational.ZERO)) {
                return Real.valueOf(0.0);
            } else {
                return arg.product(state, random);
            }
        } else {
            return random;
        }
    }
}

