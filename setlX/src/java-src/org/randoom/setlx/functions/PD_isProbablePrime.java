package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;

import java.util.List;

// isProbablePrime(integer)      : returns true if `integer' is probably prime, false if it's definitely not prime.
//                                 If true, the probability that `integer' is prime exceeds 0.999 999 999.
//                                 In other words less than 1 in 1 000 000 000 probable primes is not a prime.
//                                 Unlike isPrime() this functions is not deterministic, but has only
//                                 constant complexity.


public class PD_isProbablePrime extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isProbablePrime();

    private PD_isProbablePrime() {
        super("isProbablePrime");
        addParameter("integer");
    }

    public SetlBoolean execute(final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        final Value integer  = args.get(0);
        if (integer.isInteger() != SetlBoolean.TRUE) {
            throw new IncompatibleTypeException("Argument '" + integer + "' is not an integer.");
        }

        return SetlBoolean.get( ((Rational) integer).isProbablePrime() );
    }
}

