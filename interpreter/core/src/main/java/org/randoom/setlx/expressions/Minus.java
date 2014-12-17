package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

/**
 * Implementation of the minus operator.
 *
 * grammar rule:
 * prefixOperation
 *     : [...]
 *     | '-' factor
 *     ;
 *
 * implemented here as:
 *           ======
 *            expr
 */
public class Minus extends UnaryExpression {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Minus.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1900;

    /**
     * Constructor.
     *
     * @param expr Expression to evaluate to a number.
     */
    public Minus(final Expr expr) {
        super(expr);
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        return expr.eval(state).minus(state);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append("-");
        expr.appendBracketedExpr(state, sb, tabs, PRECEDENCE, false);
    }

    /* term operations */

    @Override
    public String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }

    /**
     * Convert a term representing a Variable expression into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting expression of this conversion.
     * @throws TermConversionException If term is malformed.
     */
    public static Minus termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 1) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr expr = TermConverter.valueToExpr(state, term.firstMember());
            return new Minus(expr);
        }
    }

    /* comparisons */

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Minus.class);

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

