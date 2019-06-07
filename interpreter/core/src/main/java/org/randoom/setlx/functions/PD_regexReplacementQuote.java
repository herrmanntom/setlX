package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SyntaxErrorException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;
import java.util.regex.Matcher;

/**
 * regexReplacementQuote(stringToBeUsedAsLiteralInRegexReplacements) : Quote a string in such a way, that its matched as literal replacement in functions working with regular expressions.
 */
public class PD_regexReplacementQuote extends PreDefinedProcedure {

    private final static ParameterDefinition STRING_TO_BE_USED_AS_LITERAL_IN_REGEX_REPLACEMENTS = createParameter("stringToBeUsedAsLiteralInRegexReplacements");

    public  final static PreDefinedProcedure DEFINITION  = new PD_regexReplacementQuote();

    private PD_regexReplacementQuote() {
        super();
        addParameter(STRING_TO_BE_USED_AS_LITERAL_IN_REGEX_REPLACEMENTS);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws IncompatibleTypeException, SyntaxErrorException {
        final Value string      = args.get(STRING_TO_BE_USED_AS_LITERAL_IN_REGEX_REPLACEMENTS);
        if ( ! (string instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Input-argument '" + string.toString(state) + "' is not a string."
            );
        }

        return new SetlString(Matcher.quoteReplacement(string.getUnquotedString(state)));
    }

}
