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
    public  final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Assignment.class);

    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1000;

    private final AssignableExpression lhs;
    private final Expr                 rhs;

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
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        rhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);

        lhs.collectVariablesWhenAssigned(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        lhs.appendString(state, sb, tabs);
        sb.append(" := ");
        rhs.appendString(state, sb, tabs);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, lhs.toTerm(state));
        result.addMember(state, rhs.toTerm(state));
        return result;
    }

    public static Assignment termToExpr(final Term term) throws TermConversionException {
        if (term.size() == 2) {
            final Expr lhs = TermConverter.valueToExpr(term.firstMember());
            final Expr rhs = TermConverter.valueToExpr(PRECEDENCE, false, term.lastMember());
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

