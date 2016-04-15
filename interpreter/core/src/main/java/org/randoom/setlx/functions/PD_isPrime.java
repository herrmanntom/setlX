package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * isPrime(integer) : Returns true if `integer' is a prime, false otherwise.
 *                    Unlike isProbablePrime() this functions actually
 *                    tests all possible factors, therefore has linear
 *                    complexity.
 */
public class PD_isPrime extends PreDefinedProcedure {

    private final static ParameterDefinition INTEGER    = createParameter("integer");

    /** Definition of the PreDefinedProcedure `isPrime'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_isPrime();

    private PD_isPrime() {
        super();
        addParameter(INTEGER);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws IncompatibleTypeException {
        final Value integer  = args.get(INTEGER);
        if (integer.isInteger() != SetlBoolean.TRUE) {
            throw new IncompatibleTypeException("Argument '" + integer + "' is not an integer.");
        }

        return SetlBoolean.valueOf( ((Rational) integer).isPrime() );

    }

}

