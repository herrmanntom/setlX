package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Iterator;
import org.randoom.setlx.utilities.IteratorExecutionContainer;


/*
grammar rules:
shortIterate
    :             iterator       '|' condition
    ;

iterate
    : anyExpr ':' iteratorChain ('|' condition)?
    ;

implemented here as:
      =======     ========-----      =========
       mExpr        mIterator        mCondition
*/

public class Iteration extends Constructor {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = "^iteration";

    private final   Expr      mExpr;
    private final   Iterator  mIterator;
    private final   Condition mCondition;

    private class Exec implements IteratorExecutionContainer {
        private final   Expr            mExpr;
        private final   Condition       mCondition;
        private final   CollectionValue mCollection;

        public Exec (final CollectionValue collection, final Expr expr, final Condition condition) {
            mCollection = collection;
            mExpr       = expr;
            mCondition  = condition;
        }

        public Value execute(final Value lastIterationValue) throws SetlException {
            if (mCondition == null || mCondition.evalToBool()) {
                if (mExpr != null) {
                    mCollection.addMember(mExpr.eval());
                } else { // is simple iteration
                    mCollection.addMember(lastIterationValue);
                }
            }
            return null;
        }
    }

    public Iteration(final Expr expr, final Iterator iterator, final Condition condition) {
        mExpr      = expr;
        mIterator  = iterator;
        mCondition = condition;
    }

    public void fillCollection(final CollectionValue collection) throws SetlException {
        mIterator.eval(new Exec(collection, mExpr, mCondition));
    }

    /* string operations */

    public void appendString(final StringBuilder sb) {
        if (mExpr != null) {
            mExpr.appendString(sb, 0);
            sb.append(" : ");
        }
        mIterator.appendString(sb);
        if (mCondition != null) {
            sb.append(" | ");
            mCondition.appendString(sb, 0);
        }
    }

    /* term operations */

    public void addToTerm(final CollectionValue collection) {
        final Term result = new Term(FUNCTIONAL_CHARACTER);
        if (mExpr != null) {
            result.addMember(mExpr.toTerm());
        } else {
            result.addMember(new SetlString("nil"));
        }
        result.addMember(mIterator.toTerm());
        if (mCondition != null) {
            result.addMember(mCondition.toTerm());
        } else {
            result.addMember(new SetlString("nil"));
        }
        collection.addMember(result);
    }

    /*package*/ static Iteration termToIteration(final Term term) throws TermConversionException {
        if (term.size() != 3) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            try {
                final Expr        expr        = TermConverter.valueToExpr(term.firstMember());

                      Iterator    iterator    = null;
                if (! term.getMember(2).equals(new SetlString("nil"))) {
                    iterator  = Iterator.valueToIterator(term.getMember(2));
                }

                      Condition   cond        = null;
                if (! term.lastMember().equals(new SetlString("nil"))) {
                    cond    = TermConverter.valueToCondition(term.lastMember());
                }
                return new Iteration(expr, iterator, cond);
            } catch (SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }
}

