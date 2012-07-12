package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;

import java.util.List;

// toLowerCase(string)           : returns String in lower case letters

public class PD_toLowerCase extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_toLowerCase();

    private PD_toLowerCase() {
        super("toLowerCase");
        addParameter("string");
    }

    public Value execute(final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        final Value string  = args.get(0);
        if ( ! (string instanceof SetlString)) {
            throw new IncompatibleTypeException("string-argument '" + string + "' is not a string.");
        }

        return new SetlString(string.getUnquotedString().toLowerCase());
    }
}

