package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.SyntaxErrorException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressionUtilities.Condition;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.SetlBoolean;
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

/**
 * grammar rule:
 * statement
 *     : [...]
 *     | 'match' '(' expr ')' '{' ('regex' expr ('as' expr)? ('|' condition)? ':' block | [...] )* ('default' ':' block)? '}'
 *     ;
 *
 * implemented here as:
 *                                         ====       ====        =========       =====
 *                                       mPattern   mAssignTo     mCondition   mStatements
 */
public class MatchRegexBranch extends MatchAbstractScanBranch {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(MatchRegexBranch.class);

    private final Expr      pattern;        // pattern to match
    private       Pattern   runtimePattern; // compiled pattern to match
    private final Expr      assignTo;       // variable to store groups
    private       Value     assignTerm;     // term of variable to store groups
    private final Condition condition;      // optional condition to confirm match
    private final Block     statements;     // block to execute after match
    private       int       endOffset;      // Offset of last match operation (i.e. how far match progressed the input)

    public MatchRegexBranch(final State state, final Expr pattern, final Expr assignTo, final Condition condition, final Block statements) {
        this(pattern, assignTo, condition, statements);

        // if pattern is static (fully optimized), it can be compiled now
        final Value patternReplacement = pattern.getReplacement();
        if (patternReplacement != null) {
            try {
                this.runtimePattern = Pattern.compile(
                    patternReplacement.getUnquotedString()
                );
            } catch (final PatternSyntaxException pse) {
                state.writeParserErrLn(
                    "Error while parsing regex-pattern " + pattern + " {\n"
                  + "\t" + pse.getDescription() + " near index " + (pse.getIndex() + 1) + "\n"
                  + "}"
                );
                state.addToParserErrorCount(1);
                this.runtimePattern = null;
            }
        } else {
            this.runtimePattern = null;
        }
    }

    private MatchRegexBranch(final Expr pattern, final Expr assignTo, final Condition condition, final Block statements) {
        this.pattern        = pattern;
        this.runtimePattern = null;
        this.assignTo       = assignTo;
        this.assignTerm     = null;
        this.condition      = condition;
        this.statements     = statements;
        this.endOffset      = -1;

        // optimize pattern
        this.pattern.optimize();
    }

    @Override
    public MatchResult matches(final State state, final Value term) throws SetlException {
        if (term instanceof SetlString) {
            final MatchResult result = scannes(state, (SetlString) term);
            if (result.isMatch() && ((SetlString) term).size() == endOffset) {
                return result;
            }
        }
        return new MatchResult(false);
    }

    @Override
    public boolean evalConditionToBool(final State state) throws SetlException {
        if (condition != null) {
            return condition.eval(state) == SetlBoolean.TRUE;
        } else {
            return true;
        }
    }

    @Override
    public MatchResult scannes(final State state, final SetlString string) throws SetlException {
        Pattern pttrn = null;

        if (runtimePattern != null) {
            pttrn = runtimePattern;
        } else {
            final Value patternStr;
            final Value patternReplacement = pattern.getReplacement();
            if (patternReplacement != null) {
                // static pattern
                patternStr = patternReplacement;
            } else {
                // dynamic pattern
                patternStr = pattern.eval(state);
            }
            if ( ! (patternStr instanceof SetlString)) {
                throw new IncompatibleTypeException(
                    "Pattern argument '" + patternStr + "' is not a string."
                );
            }
            // parse pattern
            try {
                pttrn = Pattern.compile(patternStr.getUnquotedString());
                // store pattern if it is static
                if (patternReplacement != null) {
                    runtimePattern = pttrn;
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
        final Matcher  m = pttrn.matcher(string.getUnquotedString());
        final boolean  r = m.lookingAt();
        if (r) {
            if (assignTo != null && assignTerm == null) {
                assignTerm = assignTo.toTerm(state);
            }
            endOffset = m.end();
            if (assignTerm != null) {
                final int      count  = m.groupCount() + 1;
                final SetlList groups = new SetlList(count);
                for (int i = 0; i < count; ++i) {
                    groups.addMember(state, new SetlString(m.group(i)));
                }
                return assignTerm.matchesTerm(state, groups);
            }
        } else {
            endOffset = -1;
        }
        return new MatchResult(r);
    }

    @Override
    public int getEndOffset() {
        return endOffset;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        return statements.execute(state);
    }

    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        pattern.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);

        /* Variables in this expression get assigned temporarily.
           Collect them into a temporary list, add them to boundVariables and
           remove them again before returning. */
        final List<String> tempAssigned = new ArrayList<String>();
        if (assignTo != null) {
            assignTo.collectVariablesAndOptimize(new ArrayList<String>(), tempAssigned, tempAssigned);
        }

        final int preIndex = boundVariables.size();
        boundVariables.addAll(tempAssigned);

        if (condition != null) {
            condition.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }

        statements.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);


        if (assignTo != null) {
            // remove the added variables (DO NOT use removeAll(); same variable name could be there multiple times!)
            for (int i = tempAssigned.size(); i > 0; --i) {
                boundVariables.remove(preIndex + (i - 1));
            }
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        sb.append("regex ");

        sb.append(pattern);

        if (assignTo != null) {
            sb.append(" as ");
            assignTo.appendString(state, sb, tabs);
        }

        if (condition != null) {
            sb.append(" | ");
            condition.appendString(state, sb, tabs);
        }

        sb.append(":");
        sb.append(state.getEndl());
        statements.appendString(state, sb, tabs + 1);
        sb.append(state.getEndl());
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term     result   = new Term(FUNCTIONAL_CHARACTER, 4);

        result.addMember(state, pattern.toTerm(state));

        if (assignTo != null) {
            result.addMember(state, assignTo.toTerm(state));
        } else {
            result.addMember(state, new SetlString("nil"));
        }

        if (condition != null) {
            result.addMember(state, condition.toTerm(state));
        } else {
            result.addMember(state, new SetlString("nil"));
        }

        result.addMember(state, statements.toTerm(state));

        return result;
    }

    public static MatchRegexBranch termToBranch(final Term term) throws TermConversionException {
        if (term.size() != 4) {
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

