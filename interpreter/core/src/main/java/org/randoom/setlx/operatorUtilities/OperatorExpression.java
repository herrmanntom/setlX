package org.randoom.setlx.operatorUtilities;

import org.randoom.setlx.assignments.AAssignableExpression;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operators.AOperator;
import org.randoom.setlx.operators.AZeroOperator;
import org.randoom.setlx.operators.CollectionAccessRangeDummy;
import org.randoom.setlx.operators.ProcedureConstructor;
import org.randoom.setlx.operators.SetListConstructor;
import org.randoom.setlx.operators.TermConstructor;
import org.randoom.setlx.operators.ValueOperator;
import org.randoom.setlx.operators.VariableIgnore;
import org.randoom.setlx.types.IgnoreDummy;
import org.randoom.setlx.types.Procedure;
import org.randoom.setlx.types.RangeDummy;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.Expression;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stack of operators that can be evaluated.
 */
public class OperatorExpression extends Expression {
    private final static Map<String, Method> OPERATOR_CONVERTERS = new HashMap<String, Method>();

    private FragmentList<AOperator> operators;
    private final int numberOfOperators;
    private boolean isConstant;

    /**
     * Create a new operator stack.
     *
     * @param operator Operator evaluate.
     */
    public OperatorExpression(AOperator operator) {
        this(new FragmentList<AOperator>(operator));
    }

    /**
     * Create a new operator stack.
     *
     * @param operators Operator stack to evaluate.
     */
    public OperatorExpression(FragmentList<AOperator> operators) {
        this.operators = unify(operators);
        this.numberOfOperators = operators.size();
        isConstant = false;
    }

    /**
     * Create a new operator stack from one stack and one operator.
     *
     * @param first Operator stack to evaluate.
     * @param operator Operator to append at the end.
     */
    public OperatorExpression(OperatorExpression first, AOperator operator) {
        this(new FragmentList<AOperator>(first.operators, operator));
    }

    /**
     * Create a new operator stack from two stacks and one operator.
     *
     * @param first First stack to append.
     * @param second Second stack to append.
     */
    public OperatorExpression(OperatorExpression first, OperatorExpression second) {
        this(new FragmentList<AOperator>(first.operators, second.operators));
    }

    /**
     * Create a new operator stack from two stacks and one operator.
     *
     * @param first First stack to append.
     * @param second Second stack to append.
     * @param operator Operator to append at the end.
     */
    public OperatorExpression(OperatorExpression first, OperatorExpression second, AOperator operator) {
        this(new FragmentList<AOperator>(first.operators, second.operators, operator));
    }

    @Override
    public boolean collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        if (isConstant) {
            // already marked as constant, no variables are needed during execution
            return true;
        }

        final int preBoundSize   = boundVariables.size();
        final int preUnboundSize = unboundVariables.size();
        final int preUsedSize    = usedVariables.size();

        // collect variables in this expression
        Stack<OptimizerData> optimizerFragments = new Stack<OptimizerData>();

        for (AOperator operator : operators) {
            optimizerFragments.push(operator.collectVariables(state, boundVariables, unboundVariables, usedVariables, optimizerFragments));
        }

        if (optimizerFragments.size() == 1) {
            OptimizerData optimizerData = optimizerFragments.poll();

            // prerequisite for optimization is that no variables are provided for later
            // expressions and that no unbound variables are used in this expression
            if (boundVariables.size() == preBoundSize && unboundVariables.size() == preUnboundSize) {
                // optimize when there where also no variables used at all
                if (usedVariables.size() != preUsedSize) {
                    isConstant = optimizerData.isAllowOptimization();
                }
                // or if all used variables are not prebound
                else {
                    final List<String> prebound     = boundVariables.subList(0, preBoundSize);
                    final List<String> usedHere     = new ArrayList<String>(usedVariables.subList(preUsedSize, usedVariables.size()));
                    final int          usedHereSize = usedHere.size();

                    // check if any prebound variables could have been used
                    usedHere.removeAll(prebound);
                    if (usedHere.size() == usedHereSize) {
                        // definitely not, therefore safe to optimize
                        isConstant = optimizerData.isAllowOptimization();
                    }
                }
            }

            return isConstant;
        } else {
            throw new IllegalStateException("Error in operator stack optimization!");
        }
    }

    /**
     * Data for optimization.
     */
    public static class OptimizerData {
        private boolean allowOptimization;

        /**
         * Create new OptimizerFragment.
         *
         * @param allowOptimization true iff this fragment may be optimized if it is constant
         */
        public OptimizerData(boolean allowOptimization) {
            this.allowOptimization = allowOptimization;
        }

        /**
         * @return true iff this fragment is constant and can be optimized.
         */
        public boolean isAllowOptimization() {
            return allowOptimization;
        }
    }

    /**
     * @return true iff this expression is constant, e.g. will always evaluate to the same result.
     */
    public boolean isConstant() {
        return isConstant;
    }

    /**
     * Evaluate this expression of operators.
     *
     * @param state          Current state of the running setlX program.
     * @return               Result of the evaluation.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    @Override
    public Value evaluate(final State state) throws SetlException {
        Stack<Value> values = new Stack<Value>();

        for (int i = 0; i < numberOfOperators; i++) {
            AOperator operator = operators.get(i);
            try {
                values.push(operator.evaluate(state, values));
            } catch (final SetlException se) {
                while (i < numberOfOperators) {
                    StringBuilder error = new StringBuilder();
                    error.append("Error in \"");
                    appendExpression(state, error, ++i);
                    error.append("\":");
                    se.addToTrace(error.toString());
                    while (i < numberOfOperators && operators.get(i) instanceof AZeroOperator) {
                        ++i;
                    }
                }
                throw se;
            }
        }

        return values.poll();
    }

    /**
     * Create an assignable expression from this operator expression.
     *
     * @return                             AssignableExpression.
     * @throws UndefinedOperationException if expression can not be converted.
     */
    public AAssignableExpression convertToAssignable() throws UndefinedOperationException {
        AAssignableExpression assignable = null;
        for (AOperator operator : operators) {
            assignable = unify(operator.convertToAssignableExpression(assignable));
        }
        return assignable;
    }

    /* string operations */

    @Override
    public void appendString(State state, StringBuilder sb, int tabs) {
        appendExpression(state, sb, numberOfOperators);
    }

    private void appendExpression(State state, StringBuilder sb, int maxOperatorDepth) {
        Stack<ExpressionFragment> expressionFragments = new Stack<ExpressionFragment>();

        for (int i = 0; i < maxOperatorDepth; i++) {
            AOperator operator = operators.get(i);
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

        sb.append(expressionFragments.poll().getExpression());
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

    /**
     * Precedence level in SetlX-grammar. Manly used for automatic bracket insertion
     * when printing expressions.
     *
     * (See src/grammar/OperatorPrecedences.txt)
     *
     * @return Precedence level.
     */
    public int precedence() {
        if (operators.size() > 0) {
            return operators.get(operators.size() - 1).precedence();
        } else {
            return 0;
        }
    }

    /* term operations */

    @Override
    public Value toTerm(State state) throws SetlException {
        Stack<Value> termFragments = new Stack<Value>();

        for (AOperator operator : operators) {
            termFragments.push(operator.buildTerm(state, termFragments));
        }

        return termFragments.poll();
    }

    /**
     * Generate term representing the code this expression represents, when
     * this expression is quoted ('@').
     *
     * @see org.randoom.setlx.utilities.CodeFragment#toTerm(State)
     *
     * @param state          Current state of the running setlX program.
     * @return               Generated term.
     * @throws SetlException When some error happens.
     */
    public          Value toTermQuoted(final State state) throws SetlException  {
        Stack<Value> termFragments = new Stack<Value>();

        for (AOperator operator : operators) {
            termFragments.push(operator.buildQuotedTerm(state, termFragments));
        }

        return termFragments.poll();
    }

    /**
     * Create a OperatorExpression from a (term-) value representing such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param value                    (Term-) value to convert.
     * @return                         New OperatorExpression.
     * @throws TermConversionException in case the term is malformed.
     */
    public static OperatorExpression createFromTerm(State state, Value value) throws TermConversionException {
        FragmentList<AOperator> operators = new FragmentList<AOperator>();
        appendFromTerm(state, value, operators);
        return unify(new OperatorExpression(operators));
    }

    /**
     * Create a OperatorExpression from a (term-) value representing such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param value                    (Term-) value to convert.
     * @param operatorStack            Operator stack to append to.
     * @throws TermConversionException in case the term is malformed.
     */
    public static void appendFromTerm(State state, Value value, FragmentList<AOperator> operatorStack) throws TermConversionException {
        if (value.getClass() == Term.class) {
            final Term term = (Term) value;
            final String functionalCharacter = term.getFunctionalCharacter();

            if (TermUtilities.isInternalFunctionalCharacter(functionalCharacter)) {
                Method converter;
                synchronized (OPERATOR_CONVERTERS) {
                    converter = OPERATOR_CONVERTERS.get(functionalCharacter);
                }
                if (converter == null) {
                    Class<? extends AOperator> operatorClass = TermUtilities.getClassForTerm(AOperator.class, functionalCharacter);

                    if (operatorClass != null) {
                        try {
                            converter = operatorClass.getMethod("appendToOperatorStack", State.class, Term.class, FragmentList.class);

                            synchronized (OPERATOR_CONVERTERS) {
                                OPERATOR_CONVERTERS.put(functionalCharacter, converter);
                            }
                        } catch (NoSuchMethodException e) {
                            throw new IllegalStateException("Unable to find \"appendToOperatorStacks\" in " + operatorClass.getSimpleName(), e);
                        }
                    }
                }
                // invoke method found
                if (converter != null) {
                    try {
                        converter.invoke(null, state, term, operatorStack);

                        return;

                    } catch (final InvocationTargetException ite) {
                        Throwable targetException = ite.getTargetException();
                        if (targetException instanceof TermConversionException) {
                            throw (TermConversionException) targetException;
                        }
                        throw new TermConversionException("Unknown exception during term conversion", targetException);
                    } catch (final Exception e) { // will never happen ;-)
                        // because we know this method exists etc
                        throw new TermConversionException("Impossible error...", e);
                    }
                }
            }
        } else if (value == IgnoreDummy.ID) {
            operatorStack.add(VariableIgnore.VI);

            return;

        } else if (value == RangeDummy.RD) {
            operatorStack.add(CollectionAccessRangeDummy.CARD);

            return;

        } else if (value.isList() == SetlBoolean.TRUE || value.isSet() == SetlBoolean.TRUE) {
            operatorStack.add(SetListConstructor.valueToExpr(state, value));

            return;

        }

        final Value convertedValue = Value.createFromTerm(state, value);

        if (convertedValue instanceof Procedure) {
            operatorStack.add(new ProcedureConstructor((Procedure) convertedValue));
        } else if (convertedValue.getClass() == Term.class) {
            TermConstructor.appendToOperatorStack(state, (Term) convertedValue, operatorStack);
        } else {
            operatorStack.add(new ValueOperator(convertedValue));
        }
    }

    /* comparisons */

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == OperatorExpression.class) {
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
        } else if (obj.getClass() == OperatorExpression.class) {
            return this.operators.equals(((OperatorExpression) obj).operators);
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + operators.hashCode();
    }
}
