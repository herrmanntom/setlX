package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operatorUtilities.OperatorExpression.OptimizerData;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;

import java.util.ArrayDeque;
import java.util.List;

/**
 * A operator that puts a Term on the stack.
 */
public class TermConstructor extends AZeroOperator {

    private final String fChar;             // functional character of the term
    private final int    numberOfArguments; // size of the list of arguments

    /**
     * Constructor.
     *
     * @param fChar             Functional character of the term.
     * @param numberOfArguments Size of the list of arguments (arguments are pulled form the stack)
     */
    public TermConstructor(final String fChar, int numberOfArguments) {
        this.fChar = fChar;
        this.numberOfArguments = numberOfArguments;
    }

    public int numberOfExpressionsRequiredForOperator() {
        return numberOfArguments;
    }

    @Override
    public final boolean collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public final OptimizerData collectVariables(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables, ArrayDeque<OptimizerData> optimizerData) {
        boolean allowOptimization = true;
        for (int i = 0; i < numberOfArguments; i++) {
            allowOptimization = optimizerData.poll().isAllowOptimization() && allowOptimization;
        }

        return new OptimizerData(
                allowOptimization
        );
    }

    @Override
    public Value evaluate(State state, ArrayDeque<Value> values, OperatorExpression operatorExpression, int currentStackDepth) throws SetlException {
        final Term result = new Term(fChar, numberOfArguments);

        for (int i = numberOfArguments; i > 0; i--) {
            result.setMember(state, i, values.poll().toTerm(state));
        }

        return result;
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb, List<String> expressions) {
        sb.append(fChar);
        sb.append("(");

        for (int i = 0; i < numberOfArguments; i++) {
            sb.append(expressions.get(i));
            if (i < numberOfArguments - 1) {
                sb.append(", ");
            }
        }

        sb.append(")");
    }

    @Override
    public Value modifyTerm(State state, Term term, ArrayDeque<Value> termFragments) throws SetlException {
        final Term result = new Term(fChar, numberOfArguments);

        for (int i = 0; i < numberOfArguments; i++) {
            result.setMember(state, numberOfArguments - i, termFragments.poll());
        }

        return result;
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
        final String functionalCharacter = term.getFunctionalCharacter();
        for (final Value argument : term) {
            OperatorExpression.appendFromTerm(state, argument, operatorStack);
        }
        operatorStack.add(new TermConstructor(functionalCharacter, term.size()));
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(TermConstructor.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == TermConstructor.class) {
            TermConstructor otr = (TermConstructor) other;
            //noinspection StringEquality
            if (fChar == otr.fChar && numberOfArguments == otr.numberOfArguments) {
                return 0; // clone
            }
            int cmp = fChar.compareTo(otr.fChar);
            if (cmp != 0) {
                return cmp;
            }
            return Integer.compare(numberOfArguments, otr.numberOfArguments);
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
        } else if (obj.getClass() == TermConstructor.class) {
            TermConstructor other = (TermConstructor) obj;
            //noinspection StringEquality
            if (fChar == other.fChar && numberOfArguments == other.numberOfArguments) {
                return true; // clone
            } else if (fChar.equals(other.fChar)) {
                return numberOfArguments == other.numberOfArguments;
            }
            return false;
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + fChar.hashCode();
        return hash * 31 + numberOfArguments;
    }
}
