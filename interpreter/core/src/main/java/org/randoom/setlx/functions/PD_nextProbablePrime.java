package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * nextProbablePrime(integer) : Returns the next probable prime greater as `integer'.
 *                              The probability that the returned number is not a prime
 *                              does not exceed 2**-100.
 */
public class PD_nextProbablePrime extends PreDefinedProcedure {

    private final static ParameterDef        INTEGER    = createParameter("integer");

    /** Definition of the PreDefinedProcedure `nextProbablePrime'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_nextProbablePrime();

    private PD_nextProbablePrime() {
        super();
        addParameter(INTEGER);
    }

    @Override
    public Rational execute(final State state, final HashMap<ParameterDef, Value> args) throws SetlException {
        final Value integer = args.get(INTEGER);
        if (integer.isInteger() != SetlBoolean.TRUE) {
            throw new IncompatibleTypeException("Argument '" + integer + "' is not an integer.");
        }

        return ((Rational) integer).nextProbablePrime(state);
    }
}

