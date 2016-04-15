package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * collect(list) : Collects multiple occurrences of the same value
 *                 in `list' into a map of [value, nOccurences].
 */
public class PD_collect extends PreDefinedProcedure {

    private final static ParameterDefinition LIST       = createParameter("list");

    /** Definition of the PreDefinedProcedure `collect'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_collect();

    private PD_collect() {
        super();
        addParameter(LIST);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws IncompatibleTypeException {
        final Value list = args.get(LIST);
        if ( ! (list instanceof SetlList)) {
            throw new IncompatibleTypeException(
                "Argument '" + list + "' is not a list."
            );
        }
        return ((SetlList) list).collect(state);
    }
}

