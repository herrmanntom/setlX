package org.randoom.setlx.statementBranches;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.SyntaxErrorException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.operatorUtilities.Condition;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * The regex-branch of the match or scan statement.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'match' '(' expr ')' '{' ('regex' expr ('as' expr)? ('|' condition)? ':' block | [...] )* ('default' ':' block)? '}'
 *     ;
 *
 * implemented here as:
 *                                         ====       ====        =========       =====
 *                                        pattern    assignTo     condition     statements
 */
public class MatchRegexBranch extends AbstractMatchScanBranch {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(MatchRegexBranch.class);

    private final OperatorExpression pattern;  // pattern to match
    private       Pattern runtimePattern;      // compiled pattern to match
    private final OperatorExpression assignTo; // variable to store groups
    private       Value assignTerm;            // term of variable to store groups
    private final Condition condition;         // optional condition to confirm match
    private final Block statements;            // block to execute after match

    /**
     * Create new regex-branch.
     *
     * @param state      Current state of the running setlX program.
     * @param pattern    Regex pattern to match.
     * @param assignTo   (Groups of) variable(s) to assign regex match groups to.
     * @param condition  Condition to check before execution.
     * @param statements Statements to execute when condition is met.
     */
    public MatchRegexBranch(final State state, final OperatorExpression pattern, final OperatorExpression assignTo, final Condition condition, final Block statements) {
        this(pattern, assignTo, condition, statements, state);

        // if pattern is static it can be compiled now
        if (this.pattern.isConstant()) {
            Value patternReplacement;
            try {
                patternReplacement = this.pattern.evaluate(new State());
            } catch (final Throwable t) {
                patternReplacement = null;
            }
            if (patternReplacement != null) {
                try {
                    this.runtimePattern = Pattern.compile(
                        patternReplacement.getUnquotedString(state)
                    );
                } catch (final PatternSyntaxException pse) {
                    state.writeParserErrLn(
                        "Error while parsing regex-pattern " + this.pattern.toString(state) + " {\n"
                      + "\t" + pse.getDescription() + " near index " + (pse.getIndex() + 1) + "\n"
                      + "}"
                    );
                    state.addToParserErrorCount(1);
                    this.runtimePattern = null;
                }
            } else {
                this.runtimePattern = null;
            }
        } else {
            this.runtimePattern = null;
        }
    }

    private MatchRegexBranch(final OperatorExpression pattern, final OperatorExpression assignTo, final Condition condition, final Block statements, final State state) {
        this.pattern        = pattern;
        this.runtimePattern = null;
        this.assignTo       = assignTo;
        this.assignTerm     = null;
        this.condition      = condition;
        this.statements     = statements;

        // optimize pattern
        this.pattern.optimize(state);
    }

    @Override
    public MatchResult matches(final State state, final Value term) throws SetlException {
        if (term.getClass() == SetlString.class) {
            final ScanResult result = scans(state, (SetlString) term);
            if (result.isMatch() && ((SetlString) term).size() == result.getEndOffset()) {
                return result;
            }
        }
        return new MatchResult(false);
    }

    @Override
    public boolean evalConditionToBool(final State state) throws SetlException {
        return condition == null || condition.evaluate(state) == SetlBoolean.TRUE;
    }

    @Override
    public ScanResult scans(final State state, final SetlString string) throws SetlException {
        Pattern pttrn;

        if (runtimePattern != null) {
            pttrn = runtimePattern;
        } else {
            final Value patternStr = pattern.evaluate(state);
            if (patternStr.getClass() != SetlString.class) {
                throw new IncompatibleTypeException(
                    "Pattern argument '" + patternStr.toString(state) + "' is not a string."
                );
            }
            final String p = patternStr.getUnquotedString(state);
            // parse pattern
            try {
                pttrn = Pattern.compile(p);
                // store pattern if it is static
                if (pattern.isConstant()) {
                    runtimePattern = pttrn;
                }
            } catch (final PatternSyntaxException pse) {
                final LinkedList<String> errors = new LinkedList<>();
                errors.add("Error while parsing regex-pattern '" + p + "' {");
                errors.add("\t" + pse.getDescription() + " near index " + (pse.getIndex() + 1));
                errors.add("}");
                throw SyntaxErrorException.create(
                    errors,
                    "1 syntax error encountered."
                );
            }
        }

        if (assignTo != null && assignTerm == null) {
            assignTerm = assignTo.toTerm(state);
        }

        // match pattern
        return string.matchRegexPattern(state, pttrn, false, assignTerm);
    }

    @Override
    public Block getStatements() {
        return statements;
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        pattern.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);

        /* Variables in this expression get assigned temporarily.
           Collect them into a temporary list, add them to boundVariables and
           remove them again before returning. */
        final List<String> tempAssigned = new ArrayList<>();
        if (assignTo != null) {
            assignTo.collectVariablesAndOptimize(state, new ArrayList<String>(), tempAssigned, tempAssigned);
        }

        final int preIndex = boundVariables.size();
        boundVariables.addAll(tempAssigned);

        if (condition != null) {
            condition.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }

        statements.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);


        if (assignTo != null) {
            // remove the added variables (DO NOT use removeAll(); same variable name could be there multiple times!)
            for (int i = tempAssigned.size(); i > 0; --i) {
                boundVariables.remove(preIndex + (i - 1));
            }
        }
        return false;
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        sb.append("regex ");

        pattern.appendString(state, sb, 0);

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
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 4);

        result.addMember(state, pattern.toTerm(state));

        if (assignTo != null) {
            result.addMember(state, assignTo.toTerm(state));
        } else {
            result.addMember(state, SetlString.NIL);
        }

        if (condition != null) {
            result.addMember(state, condition.toTerm(state));
        } else {
            result.addMember(state, SetlString.NIL);
        }

        result.addMember(state, statements.toTerm(state));

        return result;
    }

    /**
     * Convert a term representing a regex-branch into such a branch.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting branch.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static MatchRegexBranch termToBranch(final State state, final Term term) throws TermConversionException {
        if (term.size() != 4) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            try {
                final OperatorExpression pattern = OperatorExpression.createFromTerm(state, term.firstMember());

                OperatorExpression assignTo = null;
                if (! term.getMember(2).equals(SetlString.NIL)) {
                    assignTo = OperatorExpression.createFromTerm(state, term.getMember(2));
                }

                Condition condition = null;
                if (! term.getMember(3).equals(SetlString.NIL)) {
                    condition = TermUtilities.valueToCondition(state, term.getMember(3));
                }

                final Block block = TermUtilities.valueToBlock(state, term.lastMember());

                return new MatchRegexBranch(pattern, assignTo, condition, block, state);
            } catch (final SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == MatchRegexBranch.class) {
            MatchRegexBranch otr = (MatchRegexBranch) other;
            int cmp = pattern.compareTo(otr.pattern);
            if (cmp != 0) {
                return cmp;
            }
            cmp = statements.compareTo(otr.statements);
            if (cmp != 0) {
                return cmp;
            }
            if (assignTo != null) {
                if (otr.assignTo != null) {
                    cmp = assignTo.compareTo(otr.assignTo);
                } else {
                    return 1;
                }
            } else if (otr.assignTo != null) {
                return -1;
            }
            if (cmp != 0) {
                return cmp;
            }
            if (condition != null) {
                if (otr.condition != null) {
                    return condition.compareTo(otr.condition);
                } else {
                    return 1;
                }
            } else if (otr.condition != null) {
                return -1;
            }
            return 0;
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(MatchRegexBranch.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == MatchRegexBranch.class) {
            MatchRegexBranch otr = (MatchRegexBranch) obj;
            if (pattern.equals(otr.pattern) && statements.equals(otr.statements)) {
                boolean assignToEqual = false;
                if (assignTo != null && otr.assignTo != null) {
                    assignToEqual = assignTo.equals(otr.assignTo);
                } else if (assignTo == null && otr.assignTo == null) {
                    assignToEqual = true;
                }
                if (assignToEqual) {
                    if (condition != null && otr.condition != null) {
                        return condition.equals(otr.condition);
                    } else if (condition == null && otr.condition == null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public final int computeHashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + pattern.hashCode();
        hash = hash * 31 + statements.computeHashCode();
        if (assignTo != null) {
            hash = hash * 31 + assignTo.hashCode();
        }
        if (condition != null) {
            hash = hash * 31 + condition.hashCode();
        }
        return hash;
    }

    /**
     * Get the functional character used in terms.
     *
     * @return functional character used in terms.
     */
    /*package*/ static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}

