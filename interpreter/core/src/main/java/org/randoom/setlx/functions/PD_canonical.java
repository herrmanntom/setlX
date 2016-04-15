package org.randoom.setlx.functions;

import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * canonical(term) : Returns a string of a term in its true form.
 */
public class PD_canonical extends PreDefinedProcedure {

    private final static ParameterDefinition TERM       = createParameter("term");

    /** Definition of the PreDefinedProcedure `canonical'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_canonical();

    private PD_canonical() {
        super();
        addParameter(TERM);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) {
        final StringBuilder sb = new StringBuilder();
        args.get(TERM).canonical(state, sb);
        return SetlString.newSetlStringFromSB(sb);
    }
}

