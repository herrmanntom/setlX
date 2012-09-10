package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SyntaxErrorException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

// matches(string, pattern)      : returns true if `string' matches the regular expression pattern

public class PD_matches extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_matches();

    private PD_matches() {
        super("matches");
        addParameter("string");
        addParameter("pattern");
    }

    public Value execute(final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException, SyntaxErrorException {
        final Value string  = args.get(0);
        final Value pattern = args.get(1);
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

        try {
            return SetlBoolean.valueOf(string.getUnquotedString().matches(pattern.getUnquotedString()));
        } catch (final PatternSyntaxException pse) {
            LinkedList<String> errors = new LinkedList<String>();
            errors.add("Error while parsing regex-pattern '" + pattern.getUnquotedString() + "' {");
            errors.add("\t" + pse.getDescription() + " near index " + (pse.getIndex() + 1));
            errors.add("}");
            throw SyntaxErrorException.create(
                errors,
                "1 syntax error encountered."
            );
        }
    }
}

