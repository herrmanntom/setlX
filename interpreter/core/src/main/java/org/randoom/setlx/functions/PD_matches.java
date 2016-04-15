package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.SyntaxErrorException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operators.Variable;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.ScanResult;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *  matches(string, pattern [, captureGroups]) :
 *              returns true if `string' matches the regular expression pattern
 *              if `captureGroups' is true, the captured groups are returned instead
 */
public class PD_matches extends PreDefinedProcedure {

    private final static ParameterDefinition STRING         = createParameter("string");
    private final static ParameterDefinition PATTERN        = createParameter("pattern");
    private final static ParameterDefinition CAPTURE_GROUPS = createOptionalParameter("captureGroups", SetlBoolean.FALSE);

    /** Definition of the PreDefinedProcedure `matches'. */
    public  final static PreDefinedProcedure DEFINITION     = new PD_matches();

    private              Value               assignTerm;

    private PD_matches() {
        super();
        addParameter(STRING);
        addParameter(PATTERN);
        addParameter(CAPTURE_GROUPS);

        assignTerm = null;
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value string     = args.get(STRING);
        final Value patternStr = args.get(PATTERN);
        final Value capture    = args.get(CAPTURE_GROUPS);

        if ( ! (string instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Input-argument '" + string.toString(state) + "' is not a string."
            );
        }

        if ( ! (patternStr instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Pattern-argument '" + patternStr.toString(state) + "' is not a string."
            );
        }

        boolean captureGroups;
        if (capture instanceof SetlBoolean) {
            captureGroups = (capture == SetlBoolean.TRUE);
        } else {
            throw new IncompatibleTypeException(
                "CaptureGroups-argument '" + capture.toString(state) + "' is not a Boolean value."
            );
        }

        try {
            final SetlString str     = (SetlString) string;
            final Pattern    pattern = Pattern.compile(patternStr.getUnquotedString(state));
            if (captureGroups) {
                if (assignTerm == null) {
                    assignTerm = new OperatorExpression(new Variable("x")).toTerm(state);
                }
                final ScanResult result = str.matchRegexPattern(state, pattern, true, assignTerm);
                if (result.isMatch()) {
                    return result.getBinding("x");
                } else {
                    return new SetlList(0);
                }
            } else {
                return SetlBoolean.valueOf(str.matchRegexPattern(state, pattern, true, null).isMatch());
            }
        } catch (final PatternSyntaxException pse) {
            final LinkedList<String> errors = new LinkedList<String>();
            errors.add("Error while parsing regex-pattern '" + patternStr.getUnquotedString(state) + "' {");
            errors.add("\t" + pse.getDescription() + " near index " + (pse.getIndex() + 1));
            errors.add("}");
            throw SyntaxErrorException.create(
                errors,
                "1 syntax error encountered."
            );
        }
    }
}

