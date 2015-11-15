package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;

/**
 * Operator that computes the product of the members of the next value from the stack, using the second value as neutral element.
 */
public class ProductOfMembersBinary extends ABinaryInfixOperator {
    /** Singleton **/
    public static final ProductOfMembersBinary POMB = new ProductOfMembersBinary();

    private ProductOfMembersBinary() {}

    @Override
    public Value evaluate(State state, Stack<Value> values) throws SetlException {
        Value rhs = values.poll();
        Value lhs = values.poll();
        return rhs.productOfMembers(state, lhs);
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        sb.append(" */ ");
    }

    /**
     * Append the operator represented by a term to the supplied operator stack.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @param operatorStack            Operator to append to.
     * @throws TermConversionException If term is malformed.
     */
    public static void appendToOperatorStack(final State state, final Term term, FragmentList<AOperator> operatorStack) throws TermConversionException {
        appendToOperatorStack(state, term, operatorStack, POMB);
    }

    @Override
    public boolean isLeftAssociative() {
        return true;
    }

    @Override
    public boolean isRightAssociative() {
        return false;
    }

    @Override
    public int precedence() {
        return 1800;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(ProductOfMembersBinary.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public int computeHashCode() {
        return (int) COMPARE_TO_ORDER_CONSTANT;
    }
}
