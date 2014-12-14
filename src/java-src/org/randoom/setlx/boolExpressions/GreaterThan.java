package org.randoom.setlx.boolExpressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.BinaryExpression;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.LeftAssociativeBinaryExpression;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

/**
 * Implementation of the greater-than comparison expression.
 *
 * grammar rule:
 * comparison
 *     : expr '>' expr
 *     ;
 *
 * implemented here as:
 *       ====     ====
 *       lhs       rhs
 */
public class GreaterThan extends LeftAssociativeBinaryExpression {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(GreaterThan.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1500;

    /**
     * Create new GreaterThan.
     *
     * @param lhs Expression to evaluate and compare.
     * @param rhs Expression to evaluate and compare.
     */
    public GreaterThan(final Expr lhs, final Expr rhs) {
        super(lhs, rhs);
    }

    /*
     * Note that these comparisons do not always behave as expected when
     * the compared values are no numbers!
     *
     * For example:
     * Set comparisons are based upon the subset relation
     * a < b
     * is true, when a is a subset of b.
     *
     * However the negation of this comparison
     * !(a < b)
     * is not (in all cases) equal to
     * a >= b
     * because it is possible that a is not a subset of b AND
     * b is neither subset nor equal to a (e.g. a := {1}; b := {2}).
     * In this case
     * !(a < b)
     * would be true, but
     * a >= b
     * would be false.
     *
     * When comparing numbers
     * !(a < b)
     * would always be the same as
     * a >= b
     */

    @Override
    protected SetlBoolean evaluate(final State state) throws SetlException {
        try {
            // note: rhs and lhs swapped!
            return rhs.eval(state).isLessThan(state, lhs.eval(state));
        } catch (final SetlException se) {
            se.addToTrace("Error in substitute comparison \"" + rhs.toString(state) + " < " + rhs.toString(state) +  "\":");
            throw se;
        }
    }

    /* string operations */

    @Override
    public void appendOperator(final StringBuilder sb) {
        sb.append(" > ");
    }

    /* term operations */

    @Override
    public String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }

    /**
     * Convert a term representing a GreaterThan into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting expression.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static GreaterThan termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr lhs = TermConverter.valueToExpr(state, term.firstMember());
            final Expr rhs = TermConverter.valueToExpr(state, term.lastMember());
            return new GreaterThan(lhs, rhs);
        }
    }

    /* comparisons */

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(GreaterThan.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}

