package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.NumberValue;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 *  random([upperBound])          : Get random number between 0.0 and argument (inclusive).
 *                                  If no argument is used, 1.0 is implied.
 */
public class PD_random extends PreDefinedProcedure {

    private final static ParameterDefinition UPPER_BOUND = createOptionalParameter("upperBound", SetlDouble.ONE);

    /** Definition of the PreDefinedProcedure `random'. */
    public  final static PreDefinedProcedure DEFINITION  = new PD_random();

    private PD_random() {
        super();
        addParameter(UPPER_BOUND);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        final NumberValue random = SetlDouble.valueOf(state.getRandomDouble());
        final Value       arg    = args.get(UPPER_BOUND);
        if (arg.equalTo(Rational.ZERO)) {
            return SetlDouble.valueOf(0.0);
        } else {
            return arg.product(state, random);
        }
    }
}

