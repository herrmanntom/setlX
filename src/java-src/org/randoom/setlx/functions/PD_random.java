package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.NumberValue;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.utilities.State;

import java.util.List;

// random([real])                : Get random number between 0.0 and argument (inclusive).
//                                 If no argument is used, 1.0 is implied.

public class PD_random extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_random();

    private PD_random() {
        super();
        addParameter("numberOfChoices");
        allowFewerParameters();
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        final NumberValue random = SetlDouble.valueOf(state.getRandomDouble());
        if (args.size() == 1) {
            final Value arg = args.get(0);
            if (arg.equalTo(Rational.ZERO)) {
                return SetlDouble.valueOf(0.0);
            } else {
                return arg.product(state, random);
            }
        } else {
            return random;
        }
    }
}

