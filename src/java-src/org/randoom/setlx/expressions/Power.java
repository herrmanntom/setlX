package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

/**
 * Implementation of the power operator (**).
 *
 * grammar rule:
 * power
 *     : factor ('**' prefixOperation)?
 *     ;
 *
 * implemented here as:
 *       ======       ===============
 *        lhs            exponent
 */
public class Power extends BinaryExpression {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Power.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 2000;

    /**
     * Constructor.
     *
     * @param lhs Left hand side of the operator.
     * @param exponent Right hand side of the operator.
     */
    public Power(final Expr lhs, final Expr exponent) {
        super(lhs, exponent);
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        return lhs.eval(state).power(state, rhs.eval(state));
    }

    /* string operations */

    @Override
    public void appendOperator(final StringBuilder sb) {
        sb.append(" ** ");
    }

    /* term operations */

    @Override
    public String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }

    /**
     * Convert a term representing a Power expression into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting expression of this conversion.
     * @throws TermConversionException If term is malformed.
     */
    public static Power termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr lhs        = TermConverter.valueToExpr(state, term.firstMember());
            final Expr exponent   = TermConverter.valueToExpr(state, term.lastMember());
            return new Power(lhs, exponent);
        }
    }

    /* comparisons */

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Power.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }

    /**
     * Get the functional character used in terms.
     *
     * @return functional character used in terms.
     */
    public static String functionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}

