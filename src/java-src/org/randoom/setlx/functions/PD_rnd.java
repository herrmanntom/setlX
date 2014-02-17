package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 *  rnd(numberOrCollection [, numberOfChoices]) :
 *                              a) If argument is an integer, returns a random number
 *                                 between 0 and the argument (inclusive).
 *                              b) If the argument is a rational number,
 *                                 the `numberOfChoices' MUST be used and
 *                                 a random number between 0 and the argument
 *                                 (inclusive) will be returned. The number of
 *                                 possible values in this range will equal
 *                                 `numberOfChoices' which MUST be an integer >= 2.
 *                              c) If the argument is a collectionValue,
 *                                 a randomly selected member will be returned.
 */
public class PD_rnd extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `rnd'. */
    public final static PreDefinedProcedure DEFINITION = new PD_rnd();

    private PD_rnd() {
        super();
        addParameter("numberOrCollection");
        addParameter("numberOfChoices");
        allowFewerParameters();
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        if (args.size() == 1) {
            return args.get(0).rnd(state);
        } else if (args.size() == 2) {
            return args.get(0).rnd(state, args.get(1));
        } else {
            final StringBuilder error = new StringBuilder();
            error.append("Procedure is defined with more parameters: ");
            error.append(getName());
            error.append("(");
            parameters.get(0).appendString(state, error, 0);
            error.append(", [");
            parameters.get(1).appendString(state, error, 0);
            error.append("])");
            throw new IncorrectNumberOfParametersException(error.toString());
        }
    }
}

