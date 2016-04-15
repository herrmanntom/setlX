package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

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

    private final static ParameterDefinition NUMBER_OR_COLLECTION = createParameter("numberOrCollection");
    private final static ParameterDefinition NUMBER_OF_CHOICES    = createOptionalParameter("numberOfChoices", Om.OM);

    /** Definition of the PreDefinedProcedure `rnd'. */
    public  final static PreDefinedProcedure DEFINITION           = new PD_rnd();

    private PD_rnd() {
        super();
        addParameter(NUMBER_OR_COLLECTION);
        addParameter(NUMBER_OF_CHOICES);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        if (args.get(NUMBER_OF_CHOICES) == Om.OM) {
            return args.get(NUMBER_OR_COLLECTION).rnd(state);
        } else /* if (args.get(NUMBER_OF_CHOICES) != Om.OM) */ {
            return args.get(NUMBER_OR_COLLECTION).rnd(state, args.get(NUMBER_OF_CHOICES));
        }
    }
}

