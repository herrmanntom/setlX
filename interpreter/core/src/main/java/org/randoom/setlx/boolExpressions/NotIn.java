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
 * Implementation of the Boolean notin operator.
 *
 * grammar rule:
 * comparison
 *     : expr 'notin' expr
 *     ;
 *
 * implemented here as:
 *       ====         ====
 *       mLhs         mRhs
 */
public class NotIn extends LeftAssociativeBinaryExpression {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(NotIn.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1500;

    /**
     * Create new NotIn.
     *
     * @param lhs Expression to evaluate and locate.
     * @param rhs Expression to evaluate and search.
     */
    public NotIn(final Expr lhs, final Expr rhs) {
        super(lhs, rhs);
    }

    @Override
    protected SetlBoolean evaluate(final State state) throws SetlException {
        try {
            // note: rhs and lhs swapped!
            return rhs.eval(state).containsMember(state, lhs.eval(state)).not(state);
        } catch (final SetlException se) {
            se.addToTrace("Error in substitute comparison \"!(" + lhs.toString(state) + " in " + rhs.toString(state) +  ")\":");
            throw se;
        }
    }

    /* string operations */

    @Override
    public void appendOperator(final StringBuilder sb) {
        sb.append(" notin ");
    }

    /* term operations */

    @Override
    public String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }

    /**
     * Convert a term representing a NotIn into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting expression.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static NotIn termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr lhs = TermConverter.valueToExpr(state, term.firstMember());
            final Expr rhs = TermConverter.valueToExpr(state, term.lastMember());
            return new NotIn(lhs, rhs);
        }
    }

    /* comparisons */

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(NotIn.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}

