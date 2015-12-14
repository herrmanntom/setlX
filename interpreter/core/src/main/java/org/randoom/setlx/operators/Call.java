package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UnknownFunctionException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operatorUtilities.OperatorExpression.OptimizerData;
import org.randoom.setlx.operatorUtilities.Stack;
import org.randoom.setlx.types.*;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * Operator that evaluates a function and puts the result on the stack.
 */
public class Call extends AUnaryPostfixOperator {
    private static final String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Call.class);

    private final FragmentList<OperatorExpression> arguments;
    private final OperatorExpression listArgument;

    /**
     * Create a new call operator.
     *
     * @param arguments    Parameters to the call.
     * @param listArgument Expression to evaluate as list-argument of the call.
     */
    public Call(FragmentList<OperatorExpression> arguments, OperatorExpression listArgument) {
        this.arguments = unify(arguments);
        this.listArgument = unify(listArgument);
    }

    @Override
    public OptimizerData collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables, OptimizerData lhs) {
        for (final OperatorExpression expr : arguments) {
            expr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
        if (listArgument != null) {
            listArgument.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
        return new OptimizerData(
                false
        );
    }

    @Override
    public Value evaluate(State state, Stack<Value> values) throws SetlException {
        final Value lhs = values.poll();
        if (lhs == Om.OM) {
            throw new UnknownFunctionException(
                    "Left hand side is undefined (om)."
            );
        }
        // supply the original expressions (args), which are needed for 'rw' parameters
        try {
            return lhs.call(state, arguments, listArgument);
        } catch (final SetlException se) {
            final StringBuilder error = new StringBuilder();
            error.append("Error in \"");
            lhs.appendString(state, error, 0);
            appendOperatorSign(state, error);
            error.append("\":");
            se.addToTrace(error.toString());
            throw se;
        }
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        sb.append("(");

        arguments.appendString(state, sb);

        if (listArgument != null) {
            if ( ! arguments.isEmpty()) {
                sb.append(", ");
            }
            sb.append("*");
            listArgument.appendString(state, sb, 0);
        }

        sb.append(")");
    }

    @Override
    public Value modifyTerm(State state, Term term) throws SetlException {
        final SetlList args = new SetlList(arguments.size());
        for (final OperatorExpression arg: arguments) {
            args.addMember(state, arg.toTerm(state));
        }
        term.addMember(state, args);

        if (listArgument != null) {
            term.addMember(state, listArgument.toTerm(state));
        } else {
            term.addMember(state, SetlString.NIL);
        }
        return term;
    }

    @Override
    public Value buildQuotedTerm(State state, Stack<Value> termFragments) throws SetlException {
        Term term = new Term(FUNCTIONAL_CHARACTER, 3);

        term.addMember(state, termFragments.poll());

        final SetlList argumentTerms = new SetlList(arguments.size());
        for (final OperatorExpression arg: arguments) {
            argumentTerms.addMember(state, arg.evaluate(state).toTerm(state));
        }
        term.addMember(state, argumentTerms);

        if (listArgument != null) {
            term.addMember(state, listArgument.evaluate(state).toTerm(state));
        } else {
            term.addMember(state, SetlString.NIL);
        }

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

            FragmentList<OperatorExpression> arguments = new FragmentList<OperatorExpression>();
            for (final Value argument : (SetlList) term.getMember(2)) {
                arguments.add(OperatorExpression.createFromTerm(state, argument));
            }

            OperatorExpression listArgument = null;
            if (!term.lastMember().equals(SetlString.NIL)) {
                listArgument = OperatorExpression.createFromTerm(state, term.lastMember());
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
