package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operatorUtilities.OperatorExpression.OptimizerData;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * Base class for lazy binary infix operators, which are implemented as unary postfix operators with the right hand side
 * as an additional argument expression.
 */
public abstract class ALazyBinaryInfixOperator extends AUnaryPostfixOperator {
    private final OperatorExpression rightHandSide;

    /**
     * Create a new ALazyBinaryInfixOperator.
     *
     * @param rightHandSide Expression to evaluate lazily.
     */
    protected ALazyBinaryInfixOperator(OperatorExpression rightHandSide) {
        this.rightHandSide = rightHandSide;
    }

    /**
     * @return Expression to evaluate lazily.
     */
    protected final OperatorExpression getRightHandSide() {
        return rightHandSide;
    }

    @Override
    public final OptimizerData collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables, OptimizerData lhs) {
        return new OptimizerData(
                rightHandSide.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables)
        );
    }

    @Override
    public final void appendOperatorSign(State state, StringBuilder sb, List<String> expressions) {
        sb.append(getOperatorSign());
        boolean insertBrackets = isLeftAssociative()? rightHandSide.precedence() <= precedence() : rightHandSide.precedence() < precedence();
        if (insertBrackets) {
            sb.append("(");
        }
        rightHandSide.appendString(state, sb, 0);
        if (insertBrackets) {
            sb.append(")");
        }
    }

    /**
     * @return the operator symbol.
     */
    public abstract String getOperatorSign();

    @Override
    public final Value modifyTerm(State state, Term term) throws SetlException {
        term.addMember(state, rightHandSide.toTerm(state));
        return term;
    }

    @Override
    public final int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == this.getClass()) {
            return rightHandSide.compareTo(((ALazyBinaryInfixOperator) other).rightHandSide);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == this.getClass()) {
            return rightHandSide.equals(((ALazyBinaryInfixOperator) obj).rightHandSide);
        }
        return false;
    }

    /**
     * @return Hash code of the argument.
     */
    protected final int getArgumentHashCode() {
        return rightHandSide.hashCode();
    }
}
