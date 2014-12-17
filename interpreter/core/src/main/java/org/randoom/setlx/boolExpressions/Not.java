package org.randoom.setlx.boolExpressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.UnaryExpression;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

/**
 * Class representing a Boolean negation expression.
 *
 * grammar rule:
 * factor
 *     : '!' factor
 *     | [...]
 *     ;
 *
 * implemented here as:
 *           ======
 *            expr
 */
public class Not extends UnaryExpression {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Not.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 2200;

    /**
     * Create new Not.
     *
     * @param expr Expression to evaluate and invert.
     */
    public Not(final Expr expr) {
        super(expr);
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        return expr.eval(state).not(state);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append("!");
        expr.appendBracketedExpr(state, sb, tabs, PRECEDENCE, false);
    }

    /* term operations */

    @Override
    public String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }

    /**
     * Convert a term representing a Not into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting expression.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static Not termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 1) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr expr = TermConverter.valueToExpr(state, term.firstMember());
            return new Not(expr);
        }
    }

    /* comparisons */

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Not.class);

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

