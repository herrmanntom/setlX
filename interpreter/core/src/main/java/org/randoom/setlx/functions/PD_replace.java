package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SyntaxErrorException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.PatternSyntaxException;

/**
 * replace(string, pattern, replacement) :
 *                               Returns a string in which substrings matching
 *                               `pattern' are replaced with `replacement'.
 */
public class PD_replace extends PreDefinedProcedure {

    private final static ParameterDefinition STRING      = createParameter("string");
    private final static ParameterDefinition PATTERN     = createParameter("pattern");
    private final static ParameterDefinition REPLACEMENT = createParameter("replacement");

    /** Definition of the PreDefinedProcedure `replace'. */
    public  final static PreDefinedProcedure DEFINITION  = new PD_replace();

    private PD_replace() {
        super();
        addParameter(STRING);
        addParameter(PATTERN);
        addParameter(REPLACEMENT);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws IncompatibleTypeException, SyntaxErrorException {
        final Value string      = args.get(STRING);
        final Value pattern     = args.get(PATTERN);
        final Value replacement = args.get(REPLACEMENT);
        if ( ! (string instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Input-argument '" + string.toString(state) + "' is not a string."
            );
        }

        if ( ! (pattern instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Pattern-argument '" + pattern.toString(state) + "' is not a string."
            );
        }

        if ( ! (replacement instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Argument '" + replacement.toString(state) + "' is not a string."
            );
        }


        try {
            return new SetlString(string.getUnquotedString(state).replaceAll(pattern.getUnquotedString(state),replacement.getUnquotedString(state)));
        } catch (final PatternSyntaxException pse) {
            final LinkedList<String> errors = new LinkedList<String>();
            errors.add("Error while parsing regex-pattern '" + pattern.getUnquotedString(state) + "' {");
            errors.add("\t" + pse.getDescription() + " near index " + (pse.getIndex() + 1));
            errors.add("}");
            throw SyntaxErrorException.create(
                errors,
                "1 syntax error encountered."
            );
        }
    }

}


