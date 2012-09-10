package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;

import java.util.List;

// matches(string, pattern)      : returns true if `string' matches the regular expression pattern

public class PD_matches extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_matches();

    private PD_matches() {
        super("matches");
        addParameter("string");
        addParameter("pattern");
    }

    public Value execute(final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        Value string  = args.get(0);
        Value pattern = args.get(1);
        if ( ! (string instanceof SetlString)) {
            throw new IncompatibleTypeException (
                "Input-argument '" + string + "' is not a string."
            );
        }

        if ( ! (pattern instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Pattern-argument '" + pattern + "' is not a string."
            );
        }

        return SetlBoolean.valueOf(string.getUnquotedString().matches(pattern.getUnquotedString()));
    }
}

