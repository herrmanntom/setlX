package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Condition;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.Iterator;
import org.randoom.setlx.utilities.IteratorExecutionContainer;
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
    // continue execution of this loop in debug mode until it finishes. MAY ONLY BE SET BY ENVIRONMENT CLASS!
    public        static boolean sFinishLoop            = false;

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

        public Value execute(final Value lastIterationValue) throws SetlException {
            if (mCondition == null || mCondition.evalToBool()) {
                return mStatements.execute();
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

    protected Value exec() throws SetlException {
        final boolean finishLoop = sFinishLoop;
        if (finishLoop) { // unset, because otherwise it would be reset when this loop finishes
            Environment.setDebugFinishLoop(false);
        }
        final Value result = mIterator.eval(mExec);
        if (sFinishLoop) {
            Environment.setDebugModeActive(true);
            Environment.setDebugFinishLoop(false);
        } else if (finishLoop) {
            Environment.setDebugFinishLoop(true);
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
    public void collectVariablesAndOptimize (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        mIterator.collectVariablesAndOptimize(mExec, boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        Environment.getLineStart(sb, tabs);
        sb.append("for (");
        mIterator.appendString(sb);
        if (mCondition != null) {
            sb.append(" | ");
            mCondition.appendString(sb, tabs);
        }
        sb.append(") ");
        mStatements.appendString(sb, tabs, true);
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 3);
        result.addMember(mIterator.toTerm());
        if (mCondition != null) {
            result.addMember(mCondition.toTerm());
        } else {
            result.addMember(new SetlString("nil"));
        }
        result.addMember(mStatements.toTerm());
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
            } catch (SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }
}

