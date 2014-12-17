package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.AssignableExpression;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

/**
 * Implementation of the /= operator, on statement level.
 *
 * grammar rule:
 * assignmentOther
 *     : assignable ('/=' | [...] ) expr
 *     ;
 *
 * implemented here as:
 *       ==========                 ====
 *          lhs                     rhs
 */
public class QuotientAssignment extends AbstractAssignment {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(QuotientAssignment.class);

    /**
     * Create new QuotientAssignment.
     *
     * @param lhs Expression to assign to.
     * @param rhs Expression to evaluate.
     */
    public QuotientAssignment(final AssignableExpression lhs, final Expr rhs) {
        super(lhs, rhs);
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        final Value assigned = lhs.eval(state).quotientAssign(state, rhs.eval(state).clone());
        lhs.assignUncloned(state, assigned, FUNCTIONAL_CHARACTER);

        if (printAfterEval) {
            printResult(state, assigned);
        }

        return null;
    }

    /* string operations */

    @Override
    public void appendOperator(final StringBuilder sb) {
        sb.append(" /= ");
    }

    /* term operations */

    @Override
    public String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }

    /**
     * Convert a term representing an QuotientAssignment into such a statement.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting statement.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static QuotientAssignment termToStatement(final State state, final Term term) throws TermConversionException {
        if (term.size() == 2) {
            final Expr lhs = TermConverter.valueToExpr(state, term.firstMember());
            final Expr rhs = TermConverter.valueToExpr(state, term.lastMember());
            if (lhs instanceof AssignableExpression) {
                return new QuotientAssignment((AssignableExpression) lhs, rhs);
            }
        }
        throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
    }

    /* comparisons */

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(QuotientAssignment.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }
}

