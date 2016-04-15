package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * multiLineMode(toggle) : Only accept input after additional new line.
 */
public class PD_multiLineMode extends PreDefinedProcedure {

    private final static ParameterDefinition TOGGLE     = createParameter("toggle");

    /** Definition of the PreDefinedProcedure `multiLineMode'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_multiLineMode();

    private PD_multiLineMode() {
        super();
        addParameter(TOGGLE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws IncompatibleTypeException {
        final Value   toggle  = args.get(TOGGLE);
        if ( ! (toggle instanceof SetlBoolean)) {
            throw new IncompatibleTypeException(
                "Toggle-argument '" + toggle + "' is not a Boolean value."
            );
        }

        if (toggle == SetlBoolean.TRUE) {
            state.setMultiLineMode(true);
        } else /* if (toggle == SetlBoolean.FALSE) */ {
            state.setMultiLineMode(false);
        }

        // everything seems fine
        return toggle;
    }
}

