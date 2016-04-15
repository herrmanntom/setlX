package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * sleep(time_in_ms) : Pause execution for a number of milliseconds.
 */
public class PD_sleep extends PreDefinedProcedure {

    private final static ParameterDefinition TIME_IN_MS = createParameter("timeInMs");

    /** Definition of the PreDefinedProcedure `sleep'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_sleep();

    private PD_sleep() {
        super();
        addParameter(TIME_IN_MS);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws IncompatibleTypeException {
        Value value = args.get(TIME_IN_MS);
        if (value.isInteger() == SetlBoolean.FALSE || value.compareTo(Rational.ONE) < 0 ) {
            throw new IncompatibleTypeException(
                "Time_in_ms-argument '" + value + "' is not an integer >= 1."
            );
        }

        try {
            final int n = value.jIntValue();
            Thread.sleep(n);
        } catch (final Exception e) {
            // don't care if anything happens here...
        }

        return Om.OM;
    }
}

