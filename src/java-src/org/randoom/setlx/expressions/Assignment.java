package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/**
 * The simple direct assignment.
 *
 * grammar rule:
 * assignmentDirect
 *     : assignable ':=' (assignmentDirect | expr)
 *     ;
 *
 * implemented here as:
 *       ==========       =======================
 *           lhs                   rhs
 */
public class Assignment extends BinaryExpression {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Assignment.class);

    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1000;

    private final AssignableExpression lhs;

    /**
     * Constructor.
     *
     * @param lhs Left hand side of the assignment.
     * @param rhs Right hand side of the assignment.
     */
    public Assignment(final AssignableExpression lhs, final Expr rhs) {
        super(lhs, rhs);
        this.lhs = lhs;
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        return lhs.assign(state, rhs.eval(state).clone(), FUNCTIONAL_CHARACTER);
    }

    @Override
    protected void collectVariables (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        rhs.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);

        lhs.collectVariablesWhenAssigned(state, boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendOperator(final StringBuilder sb) {
        sb.append(" := ");
    }

    /* term operations */

    @Override
    public String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }

    /**
     * Convert a term representing a Assignment into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting Assignment Expression.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static Assignment termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() == 2) {
            final Expr lhs = TermConverter.valueToExpr(state, term.firstMember());
            final Expr rhs = TermConverter.valueToExpr(state, term.lastMember());
            if (lhs instanceof AssignableExpression) {
                return new Assignment((AssignableExpression) lhs, rhs);
            }
        }
        throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
    }

    /* comparisons */

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Assignment.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}

