package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Condition;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.ParseSetlX;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/*
grammar rule:
statement
    : [...]
    | 'match' '(' expr ')' '{' ('regex' STRING ('->' assignable)? ('|' condition)? ':' block | [...] )* ('default' ':' block)? '}'
    ;

implemented here as:
                                        =======      ==========        =========       =====
                                        mPattern     mAssignable       mCondition   mStatements
*/

public class MatchRegexBranch extends MatchAbstractBranch {
    // functional character used in terms TODO
    /*package*/ final static String FUNCTIONAL_CHARACTER = "^matchRegexBranch";

    private       Pattern     mPattern;    // regex pattern to match
    private final String      mPatternStr; // pattern used in print
    private final Expr        mAssignable; // variable to store groups
    private final Value       mAssignTerm; // term of variable to store groups
    private final Condition   mCondition;  // optional condition to confirm match
    private final Block       mStatements; // block to execute after match

    public MatchRegexBranch(final String pattern, final Expr assignable, final Condition condition, final Block statements){
        try {
            mPattern = Pattern.compile(pattern.substring(1, pattern.length() -1));
        } catch (final PatternSyntaxException pse) {
            Environment.errWriteLn(
                "Error while parsing regex-pattern " + pattern + " {\n"
              + "\t" + pse.getDescription() + " near index " + pse.getIndex() + "\n"
              + "}"
            );
            ParseSetlX.addReportedError();
            mPattern = null;
        }
        mPatternStr = pattern;
        mAssignable = assignable;
        if (assignable != null) {
            mAssignTerm = assignable.toTerm();
        } else {
            mAssignTerm = null;
        }
        mCondition  = condition;
        mStatements = statements;
    }

    public MatchResult matches(final Value term) throws IncompatibleTypeException {
        if (term instanceof SetlString) {
            final Matcher  m      = mPattern.matcher(term.getUnquotedString());
            final boolean  r      = m.matches();
            if (r && mAssignTerm != null) {
                final int      count  = m.groupCount() + 1;
                final SetlList groups = new SetlList(count);
                for (int i = 0; i < count; ++i) {
                    groups.addMember(new SetlString(m.group(i)));
                }
                return mAssignTerm.matchesTerm(groups);
            }
            return new MatchResult(r);
        }
        return new MatchResult(false);
    }

    public boolean evalConditionToBool() throws SetlException {
        if (mCondition != null) {
            return mCondition.evalToBool();
        } else {
            return true;
        }
    }

    public Value execute() throws SetlException {
        return mStatements.execute();
    }

    protected Value exec() throws SetlException {
        return execute();
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        Environment.getLineStart(sb, tabs);
        sb.append("regex ");

        sb.append(mPatternStr);

        if (mAssignable != null) {
            sb.append(" -> ");
            mAssignable.appendString(sb, tabs);
        }

        if (mCondition != null) {
            sb.append(" | ");
            mCondition.appendString(sb, tabs);
        }

        sb.append(":");
        sb.append(Environment.getEndl());
        mStatements.appendString(sb, tabs + 1);
        sb.append(Environment.getEndl());
    }

    /* term operations */

    public Term toTerm() {
        final Term     result   = new Term(FUNCTIONAL_CHARACTER, 4);

        result.addMember(new SetlString(mPatternStr));

        if (mAssignTerm != null) {
            result.addMember(mAssignTerm);
        } else {
            result.addMember(new SetlString("nil"));
        }

        if (mCondition != null) {
            result.addMember(mCondition.toTerm());
        } else {
            result.addMember(new SetlString("nil"));
        }

        result.addMember(mStatements.toTerm());

        return result;
    }

    public static MatchRegexBranch termToBranch(final Term term) throws TermConversionException {
        if (term.size() != 4 || ! (term.firstMember() instanceof SetlString)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            try {
                final String pattern = term.firstMember().getUnquotedString();
                Expr assignable = null;
                if (! term.getMember(2).equals(new SetlString("nil"))) {
                    assignable = TermConverter.valueToExpr(term.getMember(2));
                }
                Condition condition = null;
                if (! term.getMember(3).equals(new SetlString("nil"))) {
                    condition = TermConverter.valueToCondition(term.getMember(3));
                }
                final Block block = TermConverter.valueToBlock(term.lastMember());
                return new MatchRegexBranch(pattern, assignable, condition, block);
            } catch (SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }
}

