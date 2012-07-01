package org.randoom.setlx.boolExpressions;

import org.randoom.setlx.exceptions.BreakException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Condition;
import org.randoom.setlx.utilities.Iterator;
import org.randoom.setlx.utilities.IteratorExecutionContainer;
import org.randoom.setlx.utilities.TermConverter;
import org.randoom.setlx.utilities.VariableScope;


/*
grammar rule:
boolExpr
    : 'forall' '(' iteratorChain '|' condition ')'
    | [...]
    ;

implemented here as:
                   ========-----     =========
                     mIterator       mCondition
*/

public class Forall extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^forall";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1900;

    private final Iterator  mIterator;
    private final Condition mCondition;

    private class Exec implements IteratorExecutionContainer {
        private final Condition     mCondition;
        public        SetlBoolean   mResult;
        public        VariableScope mScope;

        public Exec (final Condition condition) {
            mCondition = condition;
            mResult    = SetlBoolean.TRUE;
            mScope     = null;
        }

        public void execute(final Value lastIterationValue) throws SetlException {
            mResult = mCondition.eval();
            if (mResult == SetlBoolean.FALSE) {
                mScope = VariableScope.getScope();  // save state where result is true
                throw new BreakException("forall"); // stop iteration
            }
        }
    }

    public Forall(final Iterator iterator, final Condition condition) {
        mIterator  = iterator;
        mCondition = condition;
    }

    protected SetlBoolean evaluate() throws SetlException {
        final Exec e = new Exec(mCondition);
        mIterator.eval(e);
        if (e.mResult == SetlBoolean.FALSE && e.mScope != null) {
            // retore state in which mBoolExpr is false
            VariableScope.setScope(e.mScope);
        }
        return e.mResult;
    }

    /* string operations */

    public String toString(final int tabs) {
        return "forall (" + mIterator.toString(tabs) + " | " + mCondition.toString(tabs) + ")";
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mIterator.toTerm());
        result.addMember(mCondition.toTerm());
        return result;
    }

    public static Forall termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Iterator  iterator  = Iterator.valueToIterator(term.firstMember());
            final Condition condition = TermConverter.valueToCondition(term.lastMember());
            return new Forall(iterator, condition);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}

