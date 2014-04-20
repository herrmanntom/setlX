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
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.SetlHashMap;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The exists expression.
 * Note: This expression has a 'side-effect' of setting the iterations for which
 *       the condition is true in the current scope.
 *
 * grammar rule:
 * factor
 *     : 'exists' '(' iteratorChain '|' condition ')'
 *     | [...]
 *     ;
 *
 * implemented here as:
 *                    ========-----     =========
 *                       iterator       condition
 */
public class Exists extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Exists.class);
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
            this.result             = SetlBoolean.FALSE;
            this.sideEffectBindings = null;
        }

        @Override
        public ReturnMessage execute(final State state, final Value lastIterationValue) throws SetlException {
            result = condition.eval(state);
            if (result == SetlBoolean.TRUE) {
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
    public Exists(final SetlIterator iterator, final Condition condition) {
        this.iterator           = iterator;
        this.condition          = condition;
        this.iterationVariables = null;
    }

    @Override
    protected SetlBoolean evaluate(final State state) throws SetlException {
        if (iterationVariables == null) {
            optimize();
        }
        final Exec e = new Exec(condition, iterationVariables);
        iterator.eval(state, e);
        if (e.result == SetlBoolean.TRUE && e.sideEffectBindings != null) {
            // restore state in which condition is true
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
        sb.append("exists (");
        iterator.appendString(state, sb, 0);
        sb.append(" | ");
        condition.appendString(state, sb, 0);
        sb.append(")");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, iterator.toTerm(state));
        result.addMember(state, condition.toTerm(state));
        return result;
    }

    /**
     * Convert a term representing an Exists into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting Exists expression.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static Exists termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlIterator  iterator  = SetlIterator.valueToIterator(state, term.firstMember());
            final Condition condition = TermConverter.valueToCondition(state, term.lastMember());
            return new Exists(iterator, condition);
        }
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}

