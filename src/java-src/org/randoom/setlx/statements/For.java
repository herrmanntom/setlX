package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressionUtilities.Condition;
import org.randoom.setlx.expressionUtilities.Iterator;
import org.randoom.setlx.expressionUtilities.IteratorExecutionContainer;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'for' '(' iteratorChain | condition ')' '{' block '}'
    ;

implemented here as:
                ========-----   =========         =====
                  mIterator     mCondition     mStatements
*/

public class For extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String  FUNCTIONAL_CHARACTER   = "^for";

    private final Iterator  mIterator;
    private final Condition mCondition;
    private final Block     mStatements;
    private final Exec      mExec;

    private class Exec implements IteratorExecutionContainer {
        private final Condition mCondition;
        private final Block     mStatements;

        public Exec(final Condition condition, final Block statements) {
            mCondition  = condition;
            mStatements = statements;
        }

        @Override
        public ReturnMessage execute(final State state, final Value lastIterationValue) throws SetlException {
            if (mCondition == null || mCondition.evalToBool(state)) {
                return mStatements.exec(state);
                // ContinueException and BreakException are handled by outer iterator
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
            final List<Variable> boundVariables,
            final List<Variable> unboundVariables,
            final List<Variable> usedVariables
        ) {
            if (mCondition != null) {
                mCondition.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
            }
            mStatements.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
    }

    public For(final Iterator iterator, final Condition condition, final Block statements) {
        mIterator   = iterator;
        mCondition  = condition;
        mStatements = statements;
        mExec       = new Exec(mCondition, mStatements);
    }

    @Override
    protected ReturnMessage execute(final State state) throws SetlException {
        final boolean finishLoop = state.isDebugFinishLoop;
        if (finishLoop) { // unset, because otherwise it would be reset when this loop finishes
            state.setDebugFinishLoop(false);
        }
        final ReturnMessage result = mIterator.eval(state, mExec);
        if (state.isDebugFinishLoop) {
            state.setDebugModeActive(true);
            state.setDebugFinishLoop(false);
        } else if (finishLoop) {
            state.setDebugFinishLoop(true);
        }
        return result;
    }

    /* Gather all bound and unbound variables in this statement and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       Optimize sub-expressions during this process by calling optimizeAndCollectVariables()
       when adding variables from them.
    */
    @Override
    public void collectVariablesAndOptimize (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        mIterator.collectVariablesAndOptimize(mExec, boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.getLineStart(sb, tabs);
        sb.append("for (");
        mIterator.appendString(state, sb, 0);
        if (mCondition != null) {
            sb.append(" | ");
            mCondition.appendString(state, sb, 0);
        }
        sb.append(") ");
        mStatements.appendString(state, sb, tabs, true);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 3);
        result.addMember(state, mIterator.toTerm(state));
        if (mCondition != null) {
            result.addMember(state, mCondition.toTerm(state));
        } else {
            result.addMember(state, new SetlString("nil"));
        }
        result.addMember(state, mStatements.toTerm(state));
        return result;
    }

    public static For termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 3) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            try {
                final Iterator  iterator  = Iterator.valueToIterator(term.firstMember());
                      Condition condition = null;
                if ( ! term.getMember(2).equals(new SetlString("nil"))) {
                    condition = TermConverter.valueToCondition(term.getMember(2));
                }
                final Block     block     = TermConverter.valueToBlock(term.lastMember());
                return new For(iterator, condition, block);
            } catch (final SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }
}

