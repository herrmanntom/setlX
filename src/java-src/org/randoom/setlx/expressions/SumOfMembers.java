package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/**
 * Class implementing the unary +/ operator.
 *
 * grammar rule:
 * prefixOperation
 *     : '+/' factor
 *     | [...]
 *     ;
 *
 * implemented here as:
 *            ======
 *             expr
 */
public class SumOfMembers extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(SumOfMembers.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1900;

    private final Expr expr;

    /**
     * Constructor.
     *
     * @param expr Expression to evaluate to a CollectionValue.
     */
    public SumOfMembers(final Expr expr) {
        this.expr = expr;
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        return expr.eval(state).sumOfMembers(state, Om.OM);
    }

    @Override
    protected void collectVariables (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        expr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append("+/");
        expr.appendBracketedExpr(state, sb, tabs, PRECEDENCE, false);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);
        result.addMember(state, expr.toTerm(state));
        return result;
    }

    /**
     * Convert a term representing a SumOfMembers expression into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting expression of this conversion.
     * @throws TermConversionException If term is malformed.
     */
    public static SumOfMembers termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 1) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr expr = TermConverter.valueToExpr(state, term.firstMember());
            return new SumOfMembers(expr);
        }
    }

    // precedence level in SetlX-grammar
    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}

