package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * trim(string) : Return a trimmed string.
 */
public class PD_trim extends PreDefinedProcedure {

    private final static ParameterDef        STRING     = createParameter("string");

    /** Definition of the PreDefinedProcedure `trim'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_trim();

    private PD_trim() {
        super();
        addParameter(STRING);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) throws IncompatibleTypeException {
        final Value string = args.get(STRING);
        if ( ! (string instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "String-argument '" + string.toString(state) + "' is not a string."
            );
        }
        return new SetlString(string.getUnquotedString(state).trim());
    }

}

