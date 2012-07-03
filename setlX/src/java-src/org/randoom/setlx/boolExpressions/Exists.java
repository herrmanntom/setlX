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
    : 'exists' '(' iteratorChain '|' condition ')'
    | [...]
    ;

implemented here as:
                   ========-----     =========
                     mIterator       mCondition
*/

public class Exists extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^exists";
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
            mResult    = SetlBoolean.FALSE;
            mScope     = null;
        }

        public Value execute(final Value lastIterationValue) throws SetlException {
            mResult = mCondition.eval();
            if (mResult == SetlBoolean.TRUE) {
                mScope = VariableScope.getScope();  // save state where result is true
                throw new BreakException("exists"); // stop iteration
            }
            return null;
        }
    }

    public Exists(final Iterator iterator, final Condition condition) {
        mIterator  = iterator;
        mCondition = condition;
    }

    protected SetlBoolean evaluate() throws SetlException {
        final Exec e = new Exec(mCondition);
        mIterator.eval(e);
        if (e.mResult == SetlBoolean.TRUE && e.mScope != null) {
            // restore state in which mCondition is true
            VariableScope.setScope(e.mScope);
        }
        return e.mResult;
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        sb.append("exists (");
        mIterator.appendString(sb);
        sb.append(" | ");
        mCondition.appendString(sb, tabs);
        sb.append(")");
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(mIterator.toTerm());
        result.addMember(mCondition.toTerm());
        return result;
    }

    public static Exists termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Iterator  iterator  = Iterator.valueToIterator(term.firstMember());
            final Condition condition = TermConverter.valueToCondition(term.lastMember());
            return new Exists(iterator, condition);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}

