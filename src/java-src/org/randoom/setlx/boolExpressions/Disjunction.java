package org.randoom.setlx.boolExpressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;


/**
 * Class representing a Boolean disjunction expression (e.g. 'or').
 *
 * grammar rule:
 * disjunction
 *     : conjunction ('||' conjunction)*
 *     ;
 *
 * implemented here as:
 *       ===========       ===========
 *           lhs               rhs
 */
public class Disjunction extends LazyEvaluatingLeftAssociativeBinaryExpression {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Disjunction.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1300;

    /**
     * Create new Disjunction.
     *
     * @param lhs Expression to evaluate and combine.
     * @param rhs Expression to evaluate and combine.
     */
    public Disjunction(final Expr lhs, final Expr rhs) {
        super(lhs, rhs);
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        return lhs.eval(state).disjunction(state, rhs);
    }

    @Override
    protected SetlBoolean lhsResultCausingLaziness() {
        return SetlBoolean.TRUE;
    }

    /* string operations */

    @Override
    public void appendOperator(final StringBuilder sb) {
        sb.append(" || ");
    }

    /* term operations */

    @Override
    public String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }

    /**
     * Convert a term representing a Disjunction into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting expression.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static Disjunction termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr lhs = TermConverter.valueToExpr(state, term.firstMember());
            final Expr rhs = TermConverter.valueToExpr(state, term.lastMember());
            return new Disjunction(lhs, rhs);
        }
    }

    /* comparisons */

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Disjunction.class);

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

