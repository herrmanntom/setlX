package interpreter.utilities;

import interpreter.boolExpressions.BoolExpr;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.CollectionValue;
import interpreter.types.Value;
import interpreter.utilities.Iterator;
import interpreter.utilities.IteratorExecutionContainer;

import java.util.List;

public class Iteration extends Constructor {
    private Expr     mExpr;
    private Iterator mIterator;
    private BoolExpr mBoolExpr;

    private class Exec implements IteratorExecutionContainer {
        private Expr            mExpr;
        private BoolExpr        mBoolExpr;
        private CollectionValue mCollection;

        public Exec (CollectionValue collection, Expr expr, BoolExpr boolExpr) {
            mCollection = collection;
            mExpr       = expr;
            mBoolExpr   = boolExpr;
        }

        public void execute(Value lastIterationValue) throws SetlException {
            if (mBoolExpr == null || mBoolExpr.evalToBool()) {
                if (mExpr != null) {
                    mCollection.addMember(mExpr.eval());
                } else { // is simple iteration
                    mCollection.addMember(lastIterationValue);
                }
            }
        }
    }

    public Iteration(Expr expr, Iterator iterator, BoolExpr boolExpr) {
        mExpr     = expr;
        mIterator = iterator;
        mBoolExpr = boolExpr;
    }

    public void fillCollection(CollectionValue collection) throws SetlException {
        Exec e = new Exec(collection, mExpr, mBoolExpr);
        mIterator.eval(e);
    }

    public String toString() {
        String r;
        if (mExpr != null) {
            r = mExpr + ": ";
        } else {
            r = "";
        }
        r += mIterator;
        if (mBoolExpr != null) {
            r += " | " + mBoolExpr;
        }
        return r;
    }

}

