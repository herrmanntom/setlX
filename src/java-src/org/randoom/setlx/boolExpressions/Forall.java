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
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;
import org.randoom.setlx.utilities.VariableScope;

import java.util.List;


/*
grammar rule:
boolExpr
    : 'forall' '(' iteratorChain '|' condition ')'
    | [...]
    ;

implemented here as:
                   ========-----     =========
                      iterator       condition
*/

public class Forall extends Expr {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Forall.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1900;

    private final SetlIterator  iterator;
    private final Condition     condition;

    private class Exec implements SetlIteratorExecutionContainer {
        private final Condition     condition;
        public        SetlBoolean   result;
        public        VariableScope scope;

        public Exec (final Condition condition) {
            this.condition = condition;
            this.result    = SetlBoolean.TRUE;
            this.scope     = null;
        }

        @Override
        public ReturnMessage execute(final State state, final Value lastIterationValue) throws SetlException {
            result = condition.eval(state);
            if (result == SetlBoolean.FALSE) {
                scope = state.getScope();  // save state where result is true
                return ReturnMessage.BREAK; // stop iteration
            }
            return null;
        }

        /* Gather all bound and unbound variables in this expression and its siblings
              - bound   means "assigned" in this expression
              - unbound means "not present in bound set when used"
              - used    means "present in bound set when used"
           NOTE: Use optimizeAndCollectVariables() when adding variables from
                 sub-expressions
        */
        @Override
        public void collectVariablesAndOptimize (
            final List<String> boundVariables,
            final List<String> unboundVariables,
            final List<String> usedVariables
        ) {
            condition.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
    }

    public Forall(final SetlIterator iterator, final Condition condition) {
        this.iterator  = iterator;
        this.condition = condition;
    }

    @Override
    protected SetlBoolean evaluate(final State state) throws SetlException {
        final Exec e = new Exec(condition);
        iterator.eval(state, e);
        if (e.result == SetlBoolean.FALSE && e.scope != null) {
            // restore state in which mBoolExpr is false
            state.putAllValues(e.scope, FUNCTIONAL_CHARACTER);
        }
        return e.result;
    }

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    @Override
    protected void collectVariables (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        iterator.collectVariablesAndOptimize(new Exec(condition), boundVariables, unboundVariables, usedVariables);

        // add dummy variable to prevent optimization, side effect variables cannot be optimized
        unboundVariables.add(Variable.PREVENT_OPTIMIZATION_DUMMY);
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
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, iterator.toTerm(state));
        result.addMember(state, condition.toTerm(state));
        return result;
    }

    public static Forall termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlIterator  iterator  = SetlIterator.valueToIterator(term.firstMember());
            final Condition condition = TermConverter.valueToCondition(term.lastMember());
            return new Forall(iterator, condition);
        }
    }

    // precedence level in SetlX-grammar
    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}

