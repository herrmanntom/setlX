package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;
import java.util.Locale;

/**
 * toLowerCase(string) : Returns String in lower case letters.
 */
public class PD_toLowerCase extends PreDefinedProcedure {

    private final static ParameterDef        STRING     = createParameter("string");

    /** Definition of the PreDefinedProcedure `toLowerCase'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_toLowerCase();
    
    private PD_toLowerCase() {
        super();
        addParameter(STRING);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDef, Value> args) throws IncompatibleTypeException {
        final Value value = args.get(STRING);
        if ( ! (value instanceof SetlString)) {
            throw new IncompatibleTypeException("String-argument '" + value.toString(state) + "' is not a string.");
        }

        return new SetlString(value.getUnquotedString(state).toLowerCase(Locale.getDefault()));
    }
}

