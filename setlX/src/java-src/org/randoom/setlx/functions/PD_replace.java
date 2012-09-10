package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;

import java.util.List;

// replace(string, pattern, replacement) : returns a string in wich substrings matching
//                                         `pattern' are replaced with `replacement'

public class PD_replace extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_replace();

    private PD_replace() {
        super("replace");
        addParameter("string");
        addParameter("pattern");
        addParameter("replacement");
    }

    public Value execute(final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException {
        final Value string      = args.get(0);
        final Value pattern     = args.get(1);
        final Value replacement = args.get(2);
        if ( ! (string instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Input-argument '" + string + "' is not a string."
            );
        }

        if ( ! (pattern instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Pattern-argument '" + pattern + "' is not a string."
            );
        }

        if ( ! (replacement instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Argument '" + replacement + "' is not a string."
            );
        }

        return new SetlString(string.getUnquotedString().replaceAll(pattern.getUnquotedString(),replacement.getUnquotedString()));
    }

}


