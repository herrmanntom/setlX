package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UnknownFunctionException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operatorUtilities.OperatorExpression.OptimizerData;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Operator that evaluates a function and puts the result on the stack.
 */
public class Call extends AOperator {
    private static final String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(Call.class);

    private final FragmentList<OperatorExpression> arguments;
    private final OperatorExpression listArgument;

    /**
     * Create a new call operator.
     *
     * @param arguments    Parameters to the call.
     * @param listArgument Expression to evaluate as list-argument of the call.
     */
    public Call(FragmentList<OperatorExpression> arguments, OperatorExpression listArgument) {
        this.arguments = arguments;
        this.listArgument = listArgument;
    }

    /**
     * Append arguments and the operator represented by a term to the supplied operator stack.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @param operatorStack            OperatorStack to append to.
     * @param operator                 Operator to append to the end.
     * @throws TermConversionException If term is malformed.
     */
    protected static void appendToOperatorStack(State state, Term term, FragmentList<AOperator> operatorStack, Call operator) throws TermConversionException {
        if (term.size() < 1) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            OperatorExpression.appendFromTerm(state, term.firstMember(), operatorStack);
            operatorStack.add(operator);
        }
    }

    @Override
    public boolean hasArgumentBeforeOperator() {
        return true;
    }

    public int numberOfExpressionsRequiredForOperator() {
        int numberOfExpressionsRequired = arguments.size();
        if (listArgument != null) {
            numberOfExpressionsRequired += 1;
        }
        return numberOfExpressionsRequired;
    }

    @Override
    public boolean hasArgumentAfterOperator() {
        return false;
    }

    @Override
    public final boolean collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public final OptimizerData collectVariables(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables, ArrayDeque<OptimizerData> optimizerData) {
        // we do not care about the optimization data of the lhs... just remove it from the stack
        optimizerData.poll();

        // we also do not care about the optimization data of the arguments... just remove them from the stack
        for (int i = 0; i < numberOfExpressionsRequiredForOperator(); i++) {
            optimizerData.poll();
        }

        return new OptimizerData(
                false
        );
    }

    @Override
    public Value evaluate(State state, ArrayDeque<Value> values, OperatorExpression operatorExpression, int currentStackDepth) throws SetlException {
        Value listValue = null;
        if (listArgument != null) {
            listValue = values.poll();
        }

        List<Value> argumentValues = new ArrayList<>(arguments.size());
        for (int i = 0; i < arguments.size(); i++) {
            argumentValues.add(values.poll());
        }
        Collections.reverse(argumentValues);

        final Value lhs = values.poll();
        if (lhs == Om.OM) {
            throw new UnknownFunctionException(
                    "Left hand side is undefined (om)."
            );
        }
        // supply the original expressions (arguments), which are needed for 'rw' parameters
        return lhs.call(state, argumentValues, arguments, listValue, listArgument);
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb, List<String> expressions) {
        sb.append("(");

        for (int i = 0; i < arguments.size(); i++) {
            sb.append(expressions.get(i));
            if (i < arguments.size() - 1) {
                sb.append(", ");
            }
        }

        if (listArgument != null) {
            if ( ! arguments.isEmpty()) {
                sb.append(", ");
            }
            sb.append("*");
            sb.append(expressions.get(expressions.size() -1));
        }

        sb.append(")");
    }

    @Override
    public Value buildTerm(State state, ArrayDeque<Value> termFragments) throws SetlException {
        Value listArg = SetlString.NIL;
        if (listArgument != null) {
            listArg = termFragments.poll();
        }

        final SetlList args = new SetlList(arguments.size());
        for (int i = 0; i < arguments.size(); i++) {
            args.setMember(state, arguments.size() - i, termFragments.poll());
        }

        Value lhs = termFragments.poll();

        Term term = new Term(FUNCTIONAL_CHARACTER);
        term.addMember(state, lhs);
        term.addMember(state, args);
        term.addMember(state, listArg);

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
        try {
            if (term.size() != 3 || term.getMember(2).getClass() != SetlList.class) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }

            OperatorExpression.appendFromTerm(state, term.firstMember(), operatorStack);

            final FragmentList<OperatorExpression> arguments = new FragmentList<>();
            for (final Value argument : (SetlList) term.getMember(2)) {
                arguments.add(
                        new OperatorExpression(
                                OperatorExpression.appendFromTerm(state, argument, operatorStack)
                        )
                );
            }

            OperatorExpression listArgument = null;
            if (!term.lastMember().equals(SetlString.NIL)) {
                listArgument = new OperatorExpression(
                        OperatorExpression.appendFromTerm(state, term.lastMember(), operatorStack)
                );
            }

            operatorStack.add(new Call(arguments, listArgument));
        } catch (SetlException se) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER, se);
        }
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

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Call.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Call.class) {
            final Call otr = (Call) other;
            if (arguments == otr.arguments && listArgument == otr.listArgument) {
                return 0; // clone
            }
            int cmp;
            if (listArgument != null) {
                if (otr.listArgument != null) {
                    cmp = listArgument.compareTo(otr.listArgument);
                    if (cmp != 0) {
                        return cmp;
                    }
                } else {
                    return 1;
                }
            } else if (otr.listArgument != null) {
                return -1;
            }
            return arguments.compareTo(otr.arguments);
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
        } else if (obj.getClass() == Call.class) {
            Call other = (Call) obj;
            if (arguments == other.arguments && listArgument == other.listArgument) {
                return true; // clone
            } else if (arguments.size() == other.arguments.size()) {
                if (listArgument != null) {
                    if (other.listArgument != null) {
                        if (! listArgument.equals(other.listArgument)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else if (other.listArgument != null) {
                    return false;
                }
                return arguments.equals(other.arguments);
            }
            return false;
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + arguments.hashCode();
        if (listArgument != null) {
            hash = hash * 31 + listArgument.hashCode();
        }
        return hash;
    }
}
