package interpreter.utilities;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.expressions.Expr;
import interpreter.types.CollectionValue;
import interpreter.types.SetlInt;
import interpreter.types.SetlString;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.Iterator;
import interpreter.utilities.IteratorExecutionContainer;

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
    public  final static String FUNCTIONAL_CHARACTER = "'iteration";

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
            r = mExpr.toString(tabs) + ": ";
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

    public static Iteration TermToIteration(Term term) throws TermConversionException {
        if (term.size() != 3) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            try {
                Expr        expr        = TermConverter.valueToExpr(term.getMember(new SetlInt(1)));

                Iterator    iterator    = null;
                if (! term.getMember(new SetlInt(2)).equals(new SetlString("nil"))) {
                    iterator  = Iterator.valueToIterator(term.getMember(new SetlInt(2)));
                }

                Condition   cond        = null;
                if (! term.getMember(new SetlInt(3)).equals(new SetlString("nil"))) {
                    cond    = new Condition(TermConverter.valueToExpr(term.getMember(new SetlInt(3))));
                }
                return new Iteration(expr, iterator, cond);
            } catch (SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }
}

