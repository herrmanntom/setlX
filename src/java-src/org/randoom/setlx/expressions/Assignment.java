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
public class Assignment extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Assignment.class);

    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1000;

    private final AssignableExpression lhs;
    private final Expr                 rhs;

    /**
     * Constructor.
     *
     * @param lhs Left hand side of the assignment.
     * @param rhs Right hand side of the assignment.
     */
    public Assignment(final AssignableExpression lhs, final Expr rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
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
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        lhs.appendString(state, sb, tabs);
        sb.append(" := ");
        rhs.appendBracketedExpr(state, sb, tabs, PRECEDENCE, false);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, lhs.toTerm(state));
        result.addMember(state, rhs.toTerm(state));
        return result;
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

    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}

