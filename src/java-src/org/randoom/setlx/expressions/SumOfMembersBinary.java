package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/**
 * Class implementing the binary +/ operator.
 *
 * grammar rule:
 * reduce
 *     : prefixOperation ('+/' prefixOperation | [..])*
 *     | [...]
 *     ;
 *
 * implemented here as:
 *       ===============       ===============
 *           neutral             collection
 */
public class SumOfMembersBinary extends BinaryExpression {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(SumOfMembersBinary.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1800;

    /**
     * Constructor.
     *
     * @param neutral Left hand side of the operator.
     * @param collection Right hand side of the operator.
     */
    public SumOfMembersBinary(final Expr neutral, final Expr collection) {
        super(neutral, collection);
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        return rhs.eval(state).sumOfMembers(state, lhs.eval(state));
    }

    @Override
    protected void collectVariables (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        rhs.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        Value value = null;
        if (rhs.isReplaceable()) {
            try {
                value = rhs.eval(state);
            } catch (final Throwable t) {
                value = null;
            }
        }
        if (value == null || ! (value instanceof CollectionValue) || ((CollectionValue) value).size() == 0) {
            lhs.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
    }

    /* string operations */

    @Override
    public void appendOperator(final StringBuilder sb) {
        sb.append(" +/ ");
    }

    /* term operations */

    @Override
    public String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }

    /**
     * Convert a term representing a SumOfMembersBinary expression into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting expression of this conversion.
     * @throws TermConversionException If term is malformed.
     */
    public static SumOfMembersBinary termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr neutral    = TermConverter.valueToExpr(state, term.firstMember());
            final Expr collection = TermConverter.valueToExpr(state, term.lastMember());
            return new SumOfMembersBinary(neutral, collection);
        }
    }

    /* comparisons */

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(SumOfMembersBinary.class);

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

