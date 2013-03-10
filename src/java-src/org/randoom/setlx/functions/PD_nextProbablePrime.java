package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// nextProbablePrime(integer)    : returns the next probable prime greater as `integer'.
//                                 The probability that the returned number is not a prime does
//                                 not exceed 2**-100.

public class PD_nextProbablePrime extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_nextProbablePrime();

    private PD_nextProbablePrime() {
        super();
        addParameter("integer");
    }

    @Override
    public Rational execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        final Value integer  = args.get(0);
        if (integer.isInteger() != SetlBoolean.TRUE) {
            throw new IncompatibleTypeException("Argument '" + integer + "' is not an integer.");
        }

        return ((Rational) integer).nextProbablePrime();
    }
}

