package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.SyntaxErrorException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressionUtilities.Condition;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
    private       Value       mAssignTerm;     // term of variable to store groups
    private final Condition   mCondition;      // optional condition to confirm match
    private final Block       mStatements;     // block to execute after match
    private       int         mEndOffset;      // Offset of last match operation (i.e. how far match progressed the input)

    public MatchRegexBranch(final State state, final Expr pattern, final Expr assignTo, final Condition condition, final Block statements) {
        this(pattern, assignTo, condition, statements);

        // if pattern is static (fully optimized), it can be compiled now
        final Value patternReplacement = mPattern.getReplacement();
        if (patternReplacement != null) {
            try {
                mRuntimePattern = Pattern.compile(
                    patternReplacement.getUnquotedString()
                );
            } catch (final PatternSyntaxException pse) {
                state.writeParserErrLn(
                    "Error while parsing regex-pattern " + mPattern + " {\n"
                  + "\t" + pse.getDescription() + " near index " + (pse.getIndex() + 1) + "\n"
                  + "}"
                );
                state.addToParserErrorCount(1);
                mRuntimePattern = null;
            }
        } else {
            mRuntimePattern = null;
        }
    }

    private MatchRegexBranch(final Expr pattern, final Expr assignTo, final Condition condition, final Block statements) {
        mPattern        = pattern;
        mRuntimePattern = null;
        mAssignTo       = assignTo;
        mAssignTerm     = null;
        mCondition      = condition;
        mStatements     = statements;
        mEndOffset      = -1;

        // optimize pattern
        mPattern.optimize();
    }

    @Override
    public MatchResult matches(final State state, final Value term) throws SetlException {
        if (term instanceof SetlString) {
            final MatchResult result = scannes(state, (SetlString) term);
            if (result.isMatch() && ((SetlString) term).size() == mEndOffset) {
                return result;
            }
        }
        return new MatchResult(false);
    }

    @Override
    public boolean evalConditionToBool(final State state) throws SetlException {
        if (mCondition != null) {
            return mCondition.evalToBool(state);
        } else {
            return true;
        }
    }

    @Override
    public MatchResult scannes(final State state, final SetlString string) throws SetlException {
        Pattern pattern = null;

        if (mRuntimePattern != null) {
            pattern = mRuntimePattern;
        } else {
            final Value patternStr;
            final Value patternReplacement = mPattern.getReplacement();
            if (patternReplacement != null) {
                // static pattern
                patternStr = patternReplacement;
            } else {
                // dynamic pattern
                patternStr = mPattern.eval(state);
            }
            if ( ! (patternStr instanceof SetlString)) {
                throw new IncompatibleTypeException(
                    "Pattern argument '" + patternStr + "' is not a string."
                );
            }
            // parse pattern
            try {
                pattern = Pattern.compile(patternStr.getUnquotedString());
                // store pattern if it is static
                if (patternReplacement != null) {
                    mRuntimePattern = pattern;
                }
            } catch (final PatternSyntaxException pse) {
                final LinkedList<String> errors = new LinkedList<String>();
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
            if (mAssignTo != null && mAssignTerm == null) {
                mAssignTerm = mAssignTo.toTerm(state);
            }
            mEndOffset = m.end();
            if (mAssignTerm != null) {
                final int      count  = m.groupCount() + 1;
                final SetlList groups = new SetlList(count);
                for (int i = 0; i < count; ++i) {
                    groups.addMember(state, new SetlString(m.group(i)));
                }
                return mAssignTerm.matchesTerm(state, groups);
            }
        } else {
            mEndOffset = -1;
        }
        return new MatchResult(r);
    }

    @Override
    public int getEndOffset() {
        return mEndOffset;
    }

    @Override
    public ReturnMessage exec(final State state) throws SetlException {
        return mStatements.exec(state);
    }

    @Override
    protected ReturnMessage execute(final State state) throws SetlException {
        return exec(state);
    }

    /* Gather all bound and unbound variables in this statement and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       Optimize sub-expressions during this process by calling optimizeAndCollectVariables()
       when adding variables from them.
    */
    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        mPattern.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);

        /* Variables in this expression get assigned temporarily.
           Collect them into a temporary list, add them to boundVariables and
           remove them again before returning. */
        final List<String> tempAssigned = new ArrayList<String>();
        if (mAssignTo != null) {
            mAssignTo.collectVariablesAndOptimize(new ArrayList<String>(), tempAssigned, tempAssigned);
        }

        final int preIndex = boundVariables.size();
        boundVariables.addAll(tempAssigned);

        if (mCondition != null) {
            mCondition.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }

        mStatements.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);


        if (mAssignTo != null) {
            // remove the added variables (DO NOT use removeAll(); same variable name could be there multiple times!)
            for (int i = tempAssigned.size(); i > 0; --i) {
                boundVariables.remove(preIndex + (i - 1));
            }
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.getLineStart(sb, tabs);
        sb.append("regex ");

        sb.append(mPattern);

        if (mAssignTo != null) {
            sb.append(" as ");
            mAssignTo.appendString(state, sb, tabs);
        }

        if (mCondition != null) {
            sb.append(" | ");
            mCondition.appendString(state, sb, tabs);
        }

        sb.append(":");
        sb.append(state.getEndl());
        mStatements.appendString(state, sb, tabs + 1);
        sb.append(state.getEndl());
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term     result   = new Term(FUNCTIONAL_CHARACTER, 4);

        result.addMember(state, mPattern.toTerm(state));

        if (mAssignTo != null) {
            result.addMember(state, mAssignTo.toTerm(state));
        } else {
            result.addMember(state, new SetlString("nil"));
        }

        if (mCondition != null) {
            result.addMember(state, mCondition.toTerm(state));
        } else {
            result.addMember(state, new SetlString("nil"));
        }

        result.addMember(state, mStatements.toTerm(state));

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

