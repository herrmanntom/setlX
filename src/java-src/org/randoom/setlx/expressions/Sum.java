package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;
import java.util.Set;

/**
 * An addition of two expressions.
 *
 * grammar rule:
 * sum
 *     : product ('+' product | [...])*
 *     ;
 *
 * implemented here as:
 *       =======      =======
 *         lhs          rhs
 */
public class Sum extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Sum.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1600;

    private final Expr lhs;
    private final Expr rhs;

    public Sum(final Expr lhs, final Expr rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        return lhs.eval(state).sum(state, rhs.eval(state));
    }

    @Override
    protected void collectVariables (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        lhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        rhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        lhs.appendString(state, sb, tabs);
        sb.append(" + ");
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

    /**
     * Convert a term to a Sum-object.
     *
     * @param term                     Term to convert.
     * @return                         Resulting expression of this conversion.
     * @throws TermConversionException If term is malformed.
     */
    public static Sum termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr lhs = TermConverter.valueToExpr(PRECEDENCE, false, term.firstMember());
            final Expr rhs = TermConverter.valueToExpr(PRECEDENCE, true , term.lastMember());
            return new Sum(lhs, rhs);
        }
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }

    /* Java Code generation */

    @Override
    public void appendJavaCode(
            final State         state,
            final Set<String>   header,
            final StringBuilder code,
            final int           tabs
    ) {
        lhs.appendJavaCode(state, header, code, tabs);
        code.append(".sum(state, ");
        rhs.appendJavaCode(state, header, code, tabs);
        code.append(")");
    }

    /**
     * Get the functional character used in terms for this expression.
     *
     * @return Functional character.
     */
    public static String functionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}

