package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;
import java.util.Locale;

// toLowerCase(string)           : returns String in lower case letters

public class PD_toLowerCase extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_toLowerCase();

    private PD_toLowerCase() {
        super();
        addParameter("string");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        final Value string  = args.get(0);
        if ( ! (string instanceof SetlString)) {
            throw new IncompatibleTypeException("String-argument '" + string + "' is not a string.");
        }

        return new SetlString(string.getUnquotedString().toLowerCase(Locale.getDefault()));
    }
}

