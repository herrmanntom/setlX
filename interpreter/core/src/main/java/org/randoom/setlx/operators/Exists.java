package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.operatorUtilities.Condition;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operatorUtilities.SetlIterator;
import org.randoom.setlx.operatorUtilities.SetlIteratorExecutionContainer;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.SetlHashMap;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Operator that puts true on the stack if its condition is true for any value from its iteration.
 */
public class Exists extends AZeroOperator {
    private static final String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(Exists.class);

    private final SetlIterator iterator;
    private final Condition    condition;
    private       Set<String>  iterationVariables;

    private static final class Exec implements SetlIteratorExecutionContainer {
        private final Condition          condition;
        private final Set<String>        iterationVariables;
        private       SetlBoolean        result;
        private       SetlHashMap<Value> sideEffectBindings;

        private Exec(final Condition condition, final Set<String> iterationVariables) {
            this.condition          = condition;
            this.iterationVariables = iterationVariables;
            this.result             = SetlBoolean.FALSE;
            this.sideEffectBindings = null;
        }

        @Override
        public ReturnMessage execute(final State state, final Value lastIterationValue) throws SetlException {
            result = condition.evaluate(state);
            if (result == SetlBoolean.TRUE) {
                sideEffectBindings = new SetlHashMap<>();
                for (final String variable : iterationVariables) {
                    sideEffectBindings.put(variable, state.findValue(variable));
                }
                return ReturnMessage.BREAK; // stop iteration
            }
            return null;
        }

        @Override
        public boolean collectVariablesAndOptimize (
                final State        state,
                final List<String> boundVariables,
                final List<String> unboundVariables,
                final List<String> usedVariables
        ) {
            return condition.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
    }

    /**
     * Constructor.
     *
     * @param iterator  Iteration definition.
     * @param condition Condition to evaluate.
     */
    public Exists(final SetlIterator iterator, final Condition condition) {
        this.iterator           = iterator;
        this.condition          = condition;
        this.iterationVariables = null;
    }

    @Override
    public boolean collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        final List<String> tempVariables = new ArrayList<>();
        iterator.collectVariablesAndOptimize(state, new Exec(condition, null), tempVariables, boundVariables, unboundVariables, usedVariables);

        iterationVariables = new HashSet<>(tempVariables);
        return false;
    }

    @Override
    public Value evaluate(State state, ArrayDeque<Value> values, OperatorExpression operatorExpression, int currentStackDepth) throws SetlException {
        if (iterationVariables == null) {
            optimize(state);
        }
        final Exec e = new Exec(condition, iterationVariables);
        iterator.eval(state, e);
        if (e.result == SetlBoolean.TRUE && e.sideEffectBindings != null) {
            // restore state in which condition is true
            for (final Map.Entry<String, Value> entry : e.sideEffectBindings.entrySet()) {
                state.putValue(entry.getKey(), entry.getValue(), FUNCTIONAL_CHARACTER);
            }
        } else {
            for (String variable : iterationVariables) {
                state.putValue(variable, Om.OM, FUNCTIONAL_CHARACTER);
            }
        }
        return e.result;
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb, List<String> expressions) {
        sb.append("exists(");
        iterator.appendString(state, sb, 0);
        sb.append(" | ");
        condition.appendString(state, sb, 0);
        sb.append(")");
    }

    @Override
    public Value modifyTerm(State state, Term term, ArrayDeque<Value> termFragments) throws SetlException {
        term.addMember(state, iterator.toTerm(state));
        term.addMember(state, condition.toTerm(state));
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
        } else {
            SetlIterator iterator = SetlIterator.valueToIterator(state, term.firstMember());
            final Condition condition = TermUtilities.valueToCondition(state, term.lastMember());
            operatorStack.add(new Exists(iterator, condition));
        }
    }

    @Override
    public int precedence() {
        return 1900;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Exists.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Exists.class) {
            Exists otr = (Exists) other;
            if (iterator == otr.iterator && condition == otr.condition) {
                return 0; // clone
            }
            int cmp = iterator.compareTo(otr.iterator);
            if (cmp != 0) {
                return cmp;
            }
            return condition.compareTo(otr.condition);
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
        } else if (obj.getClass() == Exists.class) {
            Exists other = (Exists) obj;
            if (iterator == other.iterator && condition == other.condition) {
                return true; // clone
            } else if (iterator.equals(other.iterator)) {
                return condition.equals(other.condition);
            }
            return false;
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + iterator.hashCode();
        return hash * 31 + condition.hashCode();
    }
}
