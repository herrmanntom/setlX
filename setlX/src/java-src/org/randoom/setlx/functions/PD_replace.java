package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SyntaxErrorException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.SetlString;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

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

    public Value execute(final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException, SyntaxErrorException {
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


        try {
            return new SetlString(string.getUnquotedString().replaceAll(pattern.getUnquotedString(),replacement.getUnquotedString()));
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


