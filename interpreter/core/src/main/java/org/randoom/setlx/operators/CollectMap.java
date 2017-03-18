package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UnknownFunctionException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operatorUtilities.OperatorExpression.OptimizerData;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.ArrayDeque;
import java.util.List;

/**
 * Operator that collects specific elements of a collection value and puts the result on the stack.
 */
public class CollectMap extends AUnaryPostfixOperator {
    private static final String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(CollectMap.class);

    private final OperatorExpression argument;

    /**
     * Create a new call operator.
     *
     * @param argument    Parameter to the call.
     */
    public CollectMap(OperatorExpression argument) {
        this.argument = argument;
    }

    @Override
    public OptimizerData collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables, OptimizerData lhs) {
        argument.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        return new OptimizerData(
                false
        );
    }

    @Override
    public Value evaluate(State state, ArrayDeque<Value> values, OperatorExpression operatorExpression, int currentStackDepth) throws SetlException {
        final Value lhs = values.poll();
        if (lhs == Om.OM) {
            throw new UnknownFunctionException(
                    "Left hand side is undefined (om)."
            );
        }
        return lhs.collectMap(state, argument.evaluate(state).clone());
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb, List<String> expressions) {
        sb.append("{");

        argument.appendString(state, sb, 0);

        sb.append("}");
    }

    @Override
    public Value modifyTerm(State state, Term term) throws SetlException {
        term.addMember(state, argument.toTerm(state));
        return term;
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
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        }
        OperatorExpression expression = OperatorExpression.createFromTerm(state, term.lastMember());
        appendToOperatorStack(state, term, operatorStack, new CollectMap(expression));
    }

    @Override
    public boolean isLeftAssociative() {
        return false;
    }

    @Override
    public boolean isRightAssociative() {
        return false;
    }

    @Override
    public int precedence() {
        return 2100;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(CollectMap.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == CollectMap.class) {
            final CollectMap otr = (CollectMap) other;
            if (argument == otr.argument) {
                return 0; // clone
            }
            return argument.compareTo(otr.argument);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == CollectMap.class) {
            return argument.equals(((CollectMap) obj).argument);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + argument.hashCode();
    }
}
