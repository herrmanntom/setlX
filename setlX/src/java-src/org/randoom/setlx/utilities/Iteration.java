package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Iterator;
import org.randoom.setlx.utilities.IteratorExecutionContainer;

import java.util.List;

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

    private Expr      mExpr;
    private Iterator  mIterator;
    private Condition mCondition;

    private class Exec implements IteratorExecutionContainer {
        private Expr            mExpr;
        private Condition       mCondition;
        private CollectionValue mCollection;

        public Exec (CollectionValue collection, Expr expr, Condition condition) {
            mCollection = collection;
            mExpr       = expr;
            mCondition  = condition;
        }

        public void execute(Value lastIterationValue) throws SetlException {
            if (mCondition == null || mCondition.evalToBool()) {
                if (mExpr != null) {
                    mCollection.addMember(mExpr.eval());
                } else { // is simple iteration
                    mCollection.addMember(lastIterationValue);
                }
            }
        }
    }

    public Iteration(Expr expr, Iterator iterator, Condition condition) {
        mExpr      = expr;
        mIterator  = iterator;
        mCondition = condition;
    }

    public void fillCollection(CollectionValue collection) throws SetlException {
        Exec e = new Exec(collection, mExpr, mCondition);
        mIterator.eval(e);
    }

    /* string operations */

    public String toString(int tabs) {
        String r;
        if (mExpr != null) {
            r = mExpr.toString(tabs) + " : ";
        } else {
            r = "";
        }
        r += mIterator.toString(tabs);
        if (mCondition != null) {
            r += " | " + mCondition.toString(tabs);
        }
        return r;
    }

    /* term operations */

    public void addToTerm(CollectionValue collection) {
        Term result = new Term(FUNCTIONAL_CHARACTER);
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

    /*package*/ static Iteration termToIteration(Term term) throws TermConversionException {
        if (term.size() != 3) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            try {
                Expr        expr        = TermConverter.valueToExpr(term.firstMember());

                Iterator    iterator    = null;
                if (! term.getMember(new Rational(2)).equals(new SetlString("nil"))) {
                    iterator  = Iterator.valueToIterator(term.getMember(new Rational(2)));
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

