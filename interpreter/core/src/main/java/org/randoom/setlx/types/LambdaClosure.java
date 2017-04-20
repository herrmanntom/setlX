package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.parameters.ParameterList;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.statements.Return;
import org.randoom.setlx.utilities.*;

/**
 * This class represents a lambda closure.
 *
 * grammar rule:
 * lambdaClosure
 *     : lambdaParameters    '|->' sum
 *     ;
 *
 * implemented here as:
 *       ----------------          ===
 *    parameters (inherited)       expr
 */
public class LambdaClosure extends Closure {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(LambdaClosure.class);

    private final OperatorExpression expr; // expression in the body of the definition; used directly only for toString() and toTerm()

    /**
     * Create new lambda definition.
     *
     * @param parameters List of parameters.
     * @param expr       lambda-expression.
     */
    public LambdaClosure(final ParameterList parameters, final OperatorExpression expr) {
        super(parameters, new Block(new Return(expr)));
        this.expr = expr;
    }

    private LambdaClosure(
            final ParameterList      parameters,
            final Block              statements,
            final OperatorExpression expr,
            final SetlHashMap<Value> closure
    ) {
        super(parameters, statements, closure);
        this.expr = expr;
    }

    @Override
    public LambdaClosure createCopy() {
        return new LambdaClosure(parameters, statements, expr, null);
    }

    @Override
    public LambdaClosure clone() {
        if (closure != null || object != null) {
            return new LambdaClosure(parameters, statements, expr, closure);
        } else {
            return this;
        }
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        object = null;
        if (closure != null && closure.size() > 0) {
            sb.append("/* ");
            closure.appendString(state, sb, 0);
            sb.append("; */ ");
        }
        if (parameters.hasSizeOfOne()) {
            parameters.appendString(state, sb);
        } else {
            sb.append("[");
            parameters.appendString(state, sb);
            sb.append("]");
        }
        sb.append(" |=> ");
        expr.appendString(state, sb, tabs);
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) throws SetlException {
        object = null;
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        result.addMember(state, parameters.toTerm(state));

        result.addMember(state, expr.toTerm(state));

        return result;
    }

    /**
     * Convert a term representing a LambdaProcedure into such a procedure.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting LambdaProcedure.
     * @throws org.randoom.setlx.exceptions.TermConversionException Thrown in case of a malformed term.
     */
    public static LambdaClosure termToValue(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2 || term.firstMember().getClass() != SetlList.class) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final ParameterList parameters = ParameterList.termFragmentToParameterList(state, term.firstMember());
            final OperatorExpression expr = OperatorExpression.createFromTerm(state, term.lastMember());
            return new LambdaClosure(parameters, expr);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        object = null;
        if (this == other) {
            return 0;
        } else if (other.getClass() == LambdaClosure.class) {
            final LambdaClosure lambdaClosure = (LambdaClosure) other;
            int cmp = parameters.compareTo(lambdaClosure.parameters);
            if (cmp != 0) {
                return cmp;
            }
            return expr.compareTo(lambdaClosure.expr);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(LambdaClosure.class);

    @Override
    public long compareToOrdering() {
        object = null;
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equalTo(final Object other) {
        object = null;
        if (this == other) {
            return true;
        } else if (other.getClass() == LambdaClosure.class) {
            final LambdaClosure lambdaClosure = (LambdaClosure) other;
            if (parameters.equals(lambdaClosure.parameters)) {
                return expr.equals(lambdaClosure.expr);
            }
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + parameters.hashCode();
    }

    /**
     * Get the functional character of this value type used in terms.
     *
     * @return Functional character of this value type.
     */
    public static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}

