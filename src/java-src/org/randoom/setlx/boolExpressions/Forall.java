package org.randoom.setlx.boolExpressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressionUtilities.Condition;
import org.randoom.setlx.expressionUtilities.SetlIterator;
import org.randoom.setlx.expressionUtilities.SetlIteratorExecutionContainer;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The forall expression.
 * Note: This expression has a 'side-effect' of setting the iteration variables for which
 *       the condition is false in the current scope.
 *
 * grammar rule:
 * factor
 *     : 'forall' '(' iteratorChain '|' condition ')'
 *     | [...]
 *     ;
 *
 * implemented here as:
 *                    ========-----     =========
 *                       iterator       condition
 */
public class Forall extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Forall.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1900;

    private final SetlIterator  iterator;
    private final Condition     condition;
    private       Set<String>   iterationVariables;

    private class Exec implements SetlIteratorExecutionContainer {
        private final Condition          condition;
        private final Set<String>        iterationVariables;
        private       SetlBoolean        result;
        private       SetlHashMap<Value> sideEffectBindings;

        public Exec (final Condition condition, final Set<String> iterationVariables) {
            this.condition          = condition;
            this.iterationVariables = iterationVariables;
            this.result             = SetlBoolean.TRUE;
            this.sideEffectBindings = null;
        }

        @Override
        public ReturnMessage execute(final State state, final Value lastIterationValue) throws SetlException {
            result = condition.eval(state);
            if (result == SetlBoolean.FALSE) {
                sideEffectBindings = new SetlHashMap<Value>();
                for (final String variable : iterationVariables) {
                    sideEffectBindings.put(variable, state.findValue(variable));
                }
                return ReturnMessage.BREAK; // stop iteration
            }
            return null;
        }

        @Override
        public void collectVariablesAndOptimize (
            final State        state,
            final List<String> boundVariables,
            final List<String> unboundVariables,
            final List<String> usedVariables
        ) {
            condition.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
    }

    /**
     * Constructor.
     *
     * @param iterator  Iteration definition.
     * @param condition Condition to evaluate.
     */
    public Forall(final SetlIterator iterator, final Condition condition) {
        this.iterator           = iterator;
        this.condition          = condition;
        this.iterationVariables = null;
    }

    @Override
    protected SetlBoolean evaluate(final State state) throws SetlException {
        if (iterationVariables == null) {
            optimize(state);
        }
        final Exec e = new Exec(condition, iterationVariables);
        iterator.eval(state, e);
        if (e.result == SetlBoolean.FALSE && e.sideEffectBindings != null) {
            // restore state in which condition is false
            for (final Map.Entry<String, Value> entry : e.sideEffectBindings.entrySet()) {
                state.putValue(entry.getKey(), entry.getValue(), FUNCTIONAL_CHARACTER);
            }
        }
        return e.result;
    }

    @Override
    protected void collectVariables (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        final List<String> tempVariables = new ArrayList<String>();
        iterator.collectVariablesAndOptimize(state, new Exec(condition, null), tempVariables, boundVariables, unboundVariables, usedVariables);

        // add dummy variable to prevent optimization, side effect variables cannot be optimized
        unboundVariables.add(Variable.getPreventOptimizationDummy());

        iterationVariables = new HashSet<String>(tempVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append("forall (");
        iterator.appendString(state, sb, 0);
        sb.append(" | ");
        condition.appendString(state, sb, 0);
        sb.append(")");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, iterator.toTerm(state));
        result.addMember(state, condition.toTerm(state));
        return result;
    }

    /**
     * Convert a term representing a Forall into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting Forall expression.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static Forall termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlIterator  iterator  = SetlIterator.valueToIterator(state, term.firstMember());
            final Condition condition = TermConverter.valueToCondition(state, term.lastMember());
            return new Forall(iterator, condition);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Forall.class) {
            final Forall otr = (Forall) other;
            int cmp = iterator.compareTo(otr.iterator);
            if (cmp != 0) {
                return cmp;
            }
            return condition.compareTo(otr.condition);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Forall.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == Forall.class) {
            final Forall other = (Forall) obj;
            return iterator.equals(other.iterator) && condition.equals(other.condition);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + iterator.hashCode();
        hash = hash * 31 + condition.hashCode();
        return hash;
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}

