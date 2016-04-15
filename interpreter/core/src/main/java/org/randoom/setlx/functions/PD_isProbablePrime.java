package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * isProbablePrime(integer) : Returns true if `integer' is probably prime, false if it's definitely not prime.
 *                            If true, the probability that `integer' is prime exceeds 0.999 999 999.
 *                            In other words less than 1 in 1 000 000 000 probable primes is not a prime.
 *                            Unlike isPrime() this functions is not deterministic, but has only
 *                            constant complexity.
 */
public class PD_isProbablePrime extends PreDefinedProcedure {

    private final static ParameterDefinition INTEGER    = createParameter("integer");

    /** Definition of the PreDefinedProcedure `isProbablePrime'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_isProbablePrime();

    private PD_isProbablePrime() {
        super();
        addParameter(INTEGER);
    }

    @Override
    public SetlBoolean execute(final State state, final HashMap<ParameterDefinition, Value> args) throws IncompatibleTypeException {
        final Value integer  = args.get(INTEGER);
        if (integer.isInteger() != SetlBoolean.TRUE) {
            throw new IncompatibleTypeException("Argument '" + integer + "' is not an integer.");
        }

        return SetlBoolean.valueOf( ((Rational) integer).isProbablePrime() );
    }
}

