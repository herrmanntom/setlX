package org.randoom.setlx.boolExpressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.BinaryExpression;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

/**
 * Implementation of the Boolean not-equal operator.
 *
 * grammar rule:
 * comparison
 *     : expr '!=' expr
 *    ;

 * implemented here as:
 *       ====      ====
 *       lhs       rhs
 */
public class NotEqual extends BinaryExpression {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(NotEqual.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1500;

    /**
     * Create new NotEqual.
     *
     * @param lhs Expression to evaluate and compare.
     * @param rhs Expression to evaluate and compare.
     */
    public NotEqual(final Expr lhs, final Expr rhs) {
        super(lhs, rhs);
    }

    @Override
    protected SetlBoolean evaluate(final State state) throws SetlException {
        try {
            return lhs.eval(state).isEqualTo(state, rhs.eval(state)).not(state);
        } catch (final SetlException se) {
            se.addToTrace("Error in substitute comparison \"!(" + lhs.toString(state) + " == " + rhs.toString(state) +  ")\":");
            throw se;
        }
    }

    /* string operations */

    @Override
    public void appendOperator(final StringBuilder sb) {
        sb.append(" != ");
    }

    /* term operations */

    @Override
    public String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }

    /**
     * Convert a term representing a NotEqual into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting expression.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static NotEqual termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr lhs = TermConverter.valueToExpr(state, term.firstMember());
            final Expr rhs = TermConverter.valueToExpr(state, term.lastMember());
            return new NotEqual(lhs, rhs);
        }
    }

    /* comparisons */

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(NotEqual.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}

