package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.SyntaxErrorException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.LiteralConstructor;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Condition;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.ParseSetlX;
import org.randoom.setlx.utilities.TermConverter;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/*
grammar rule:
statement
    : [...]
    | 'match' '(' expr ')' '{' ('regex' expr ('as' expr)? ('|' condition)? ':' block | [...] )* ('default' ':' block)? '}'
    ;

implemented here as:
                                        ====       ====        =========       =====
                                      mPattern   mAssignTo     mCondition   mStatements
*/

public class MatchRegexBranch extends MatchAbstractScanBranch {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = "^matchRegexBranch";

    private final Expr        mPattern;        // pattern to match
    private       Pattern     mRuntimePattern; // compiled pattern to match
    private final Expr        mAssignTo;       // variable to store groups
    private final Value       mAssignTerm;     // term of variable to store groups
    private final Condition   mCondition;      // optional condition to confirm match
    private final Block       mStatements;     // block to execute after match
    private       int         mEndOffset;      // Offset of last match operation (i.e. how far match progressed the input)

    public MatchRegexBranch(final Expr pattern, final Expr assignTo, final Condition condition, final Block statements) {
        mPattern  = pattern;
        if (mPattern instanceof LiteralConstructor) {
            try {
                mRuntimePattern = Pattern.compile(
                    ((LiteralConstructor) mPattern).eval().getUnquotedString()
                );
            } catch (final PatternSyntaxException pse) {
                Environment.writeParserErrLn(
                    "Error while parsing regex-pattern " + mPattern + " {\n"
                  + "\t" + pse.getDescription() + " near index " + (pse.getIndex() + 1) + "\n"
                  + "}"
                );
                ParseSetlX.addReportedError();
                mRuntimePattern = null;
            }
        } else {
            mRuntimePattern = null;
        }
        mAssignTo = assignTo;
        if (assignTo != null) {
            mAssignTerm = assignTo.toTerm();
        } else {
            mAssignTerm = null;
        }
        mCondition  = condition;
        mStatements = statements;
        mEndOffset  = -1;
    }

    public MatchResult matches(final Value term) throws SetlException {
        if (term instanceof SetlString) {
            final MatchResult result = scannes((SetlString) term);
            if (result.isMatch() && ((SetlString) term).size() == mEndOffset) {
                return result;
            }
        }
        return new MatchResult(false);
    }

    public MatchResult scannes(final SetlString string) throws SetlException {
        Pattern pattern = null;

        if (mRuntimePattern != null) {
            pattern = mRuntimePattern;
        } else {final Value patternStr = mPattern.eval();
            if ( ! (patternStr instanceof SetlString)) {
                throw new IncompatibleTypeException(
                    "Pattern argument '" + patternStr + "' is not a string."
                );
            }
            // parse pattern
            try {
                pattern = Pattern.compile(patternStr.getUnquotedString());
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

        // match pattern
        final Matcher  m = pattern.matcher(string.getUnquotedString());
        final boolean  r = m.lookingAt();
        if (r) {
            mEndOffset = m.end();
            if (mAssignTerm != null) {
                final int      count  = m.groupCount() + 1;
                final SetlList groups = new SetlList(count);
                for (int i = 0; i < count; ++i) {
                    groups.addMember(new SetlString(m.group(i)));
                }
                return mAssignTerm.matchesTerm(groups);
            }
        } else {
            mEndOffset = -1;
        }
        return new MatchResult(r);
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

    public int getEndOffset() {
        return mEndOffset;
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        Environment.getLineStart(sb, tabs);
        sb.append("regex ");

        sb.append(mPattern);

        if (mAssignTo != null) {
            sb.append(" as ");
            mAssignTo.appendString(sb, tabs);
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

        result.addMember(mPattern.toTerm());

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
                final Expr pattern = TermConverter.valueToExpr(term.firstMember());
                Expr assignTo = null;
                if (! term.getMember(2).equals(new SetlString("nil"))) {
                    assignTo = TermConverter.valueToExpr(term.getMember(2));
                }
                Condition condition = null;
                if (! term.getMember(3).equals(new SetlString("nil"))) {
                    condition = TermConverter.valueToCondition(term.getMember(3));
                }
                final Block block = TermConverter.valueToBlock(term.lastMember());
                return new MatchRegexBranch(pattern, assignTo, condition, block);
            } catch (final SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }
}

