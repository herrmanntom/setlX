package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UnknownFunctionException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

/**
 * Expression that collects specific members of a collection value.
 *
 * grammar rule:
 * call
 *     : variable ('(' callParameters ')')? ('[' collectionAccessParams ']' | '{' anyExpr '}')*
 *     ;
 *
 * implemented here as:
 *       ==================================                                       =======
 *                    lhs                                                           arg
 */
public class CollectMap extends BinaryExpression {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(CollectMap.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1900;

    /**
     * Create a new CollectMap expression.
     *
     * @param lhs Left hand side to evaluate before collecting on its result.
     * @param arg Expression to evaluate as argument to collect.
     */
    public CollectMap(final Expr lhs, final Expr arg) {
        super(lhs, arg);
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        final Value lhs = this.lhs.eval(state);
        if (lhs == Om.OM) {
            throw new UnknownFunctionException(
                "Left hand side \"" + this.lhs + "\" is undefined."
            );
        }
        return lhs.collectMap(state, rhs.eval(state).clone());
    }

    /* string operations */

    @Override
    public void appendOperator(final StringBuilder sb) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        lhs.appendString(state, sb, tabs);
        sb.append("{");
        rhs.appendString(state, sb, tabs);
        sb.append("}");
    }

    /* term operations */

    @Override
    public String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }

    @Override
    public Term toTermQuoted(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, lhs.toTermQuoted(state));
        result.addMember(state, rhs.eval(state).toTerm(state));
        return result;
    }

    /**
     * Convert a term representing a CollectMap expression into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting CollectMap expression.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static CollectMap termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr lhs = TermConverter.valueToExpr(state, term.firstMember());
            final Expr arg = TermConverter.valueToExpr(state, term.lastMember());
            return new CollectMap(lhs, arg);
        }
    }

    /* comparisons */

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(CollectMap.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}

