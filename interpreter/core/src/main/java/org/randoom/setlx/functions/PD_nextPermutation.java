package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * nextPermutation(list) : Returns the next permutation of the list,
 *                         om if there `list' already is the last permutation.
 */
public class PD_nextPermutation extends PreDefinedProcedure {

    private final static ParameterDefinition LIST       = createParameter("list");

    /** Definition of the PreDefinedProcedure `nextPermutation'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_nextPermutation();

    private PD_nextPermutation() {
        super();
        addParameter(LIST);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {

        return args.get(LIST).nextPermutation(state);

    }

}

