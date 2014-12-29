package org.randoom.setlx.operatorUtilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operators.AOperator;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.ImmutableCodeFragment;
import org.randoom.setlx.utilities.State;

import java.util.LinkedList;
import java.util.List;

/**
 * Stack of operators that can be evaluated.
 */
public class OperatorExpression extends ImmutableCodeFragment {
    private FragmentList<AOperator> operators;

    /**
     * Create a new operator stack.
     *
     * @param operators Operator stack to evaluate.
     */
    public OperatorExpression(FragmentList<AOperator> operators) {
        this.operators = unify(operators);
    }

    @Override
    public void collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        throw new IllegalStateException("Not implemented");
    }

    /**
     * Evaluate this expression of operators.
     *
     * @param state          Current state of the running setlX program.
     * @return               Result of the evaluation.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value evaluate(final State state) throws SetlException {
        ValueStack values = new ValueStack();

        for (AOperator operator : operators) {
            values.push(operator.evaluate(state, values));
        }

        if (values.size() == 1) {
            return values.poll();
        } else {
            throw new IllegalStateException("Error in operator stack evaluation!");
        }
    }

    /* string operations */

    @Override
    public void appendString(State state, StringBuilder sb, int tabs) {
        LinkedList<ExpressionFragment> expressionFragments = new LinkedList<ExpressionFragment>();

        for (AOperator operator : operators) {
            ExpressionFragment rhs = null;
            if (operator.hasArgumentAfterOperator()) {
                rhs = expressionFragments.poll();
            }
            ExpressionFragment lhs = null;
            if (operator.hasArgumentBeforeOperator()) {
                lhs = expressionFragments.poll();
            }
            StringBuilder expressionFragment = new StringBuilder();
            if (lhs != null) {
                boolean insertBrackets = operator.isRightAssociative()? lhs.getPrecedence() <= operator.precedence() : lhs.getPrecedence() < operator.precedence();
                if (insertBrackets) {
                    expressionFragment.append("(");
                }
                expressionFragment.append(lhs.getExpression());
                if (insertBrackets) {
                    expressionFragment.append(")");
                }
            }
            operator.appendOperatorSign(state, expressionFragment);
            if (rhs != null) {
                boolean insertBrackets = operator.isLeftAssociative()? rhs.getPrecedence() <= operator.precedence() : rhs.getPrecedence() < operator.precedence();
                if (insertBrackets) {
                    expressionFragment.append("(");
                }
                expressionFragment.append(rhs.getExpression());
                if (insertBrackets) {
                    expressionFragment.append(")");
                }
            }
            expressionFragments.push(new ExpressionFragment(expressionFragment.toString(), operator.precedence()));
        }

        if (expressionFragments.size() == 1) {
            sb.append(expressionFragments.poll().getExpression());
        } else {
            throw new IllegalStateException("Error in operator printing!");
        }
    }

    private static class ExpressionFragment {
        private String  expression;
        private int     precedence;

        public ExpressionFragment(String expression, int precedence) {
            this.expression      = expression;
            this.precedence      = precedence;
        }

        public String getExpression() {
            return expression;
        }

        public int getPrecedence() {
            return precedence;
        }
    }

    /* term operations */

    @Override
    public Value toTerm(State state) throws SetlException {
        ValueStack termFragments = new ValueStack();

        for (AOperator operator : operators) {
            termFragments.push(operator.buildTerm(state, termFragments));
        }

        if (termFragments.size() == 1) {
            return termFragments.poll();
        } else {
            throw new IllegalStateException("Error in operator toTerm!");
        }
    }

    /* comparisons */

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == OperatorExpression.class || other.getClass() == AssignableOperatorExpression.class) {
            final OperatorExpression otr = (OperatorExpression) other;
            return operators.compareTo(otr.operators);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(OperatorExpression.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == OperatorExpression.class || obj.getClass() == AssignableOperatorExpression.class) {
            return this.operators.equals(((OperatorExpression) obj).operators);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + operators.hashCode();
    }
}
