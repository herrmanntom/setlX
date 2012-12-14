package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.SyntaxErrorException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.utilities.State;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

// matches(string, pattern [, captureGroups]) : returns true if `string' matches the regular expression pattern
//                                              if `captureGroups' is true, the captured groups are returned instead

public class PD_matches extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_matches();

    private PD_matches() {
        super("matches");
        addParameter("string");
        addParameter("pattern");
        addParameter("captureGroups");
        allowFewerParameters();
    }

    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        if (args.size() < 2) {
            throw new IncorrectNumberOfParametersException(
                "Procedure is defined with a larger number of parameters (2 or 3)."
            );
        }
        final Value string     = args.get(0);
        final Value patternStr = args.get(1);
        final Value capture    = (args.size() == 3)? args.get(2) : null;

        if ( ! (string instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Input-argument '" + string + "' is not a string."
            );
        }

        if ( ! (patternStr instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Pattern-argument '" + patternStr + "' is not a string."
            );
        }

        boolean captureGroups = false;
        if (capture != null && capture instanceof SetlBoolean) {
            captureGroups = (capture == SetlBoolean.TRUE);
        } else if (capture != null) {
            throw new IncompatibleTypeException(
                "CaptureGroups-argument '" + capture + "' is not a Boolean value."
            );
        }

        try {
            if (captureGroups) {
                final Pattern pattern = Pattern.compile(patternStr.getUnquotedString());
                final Matcher matcher = pattern.matcher(string.getUnquotedString());
                if (matcher.matches()) {
                    final int      count  = matcher.groupCount() + 1;
                    final SetlList groups = new SetlList(count);
                    for (int i = 0; i < count; ++i) {
                        groups.addMember(new SetlString(matcher.group(i)));
                    }
                    return groups;
                } else {
                    return new SetlList(0);
                }
            } else {
                return SetlBoolean.valueOf(string.getUnquotedString().matches(patternStr.getUnquotedString()));
            }
        } catch (final PatternSyntaxException pse) {
            LinkedList<String> errors = new LinkedList<String>();
            errors.add("Error while parsing regex-pattern '" + patternStr.getUnquotedString() + "' {");
            errors.add("\t" + pse.getDescription() + " near index " + (pse.getIndex() + 1));
            errors.add("}");
            throw SyntaxErrorException.create(
                errors,
                "1 syntax error encountered."
            );
        }
    }
}

