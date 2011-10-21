package interpreter.boolExpressions;

import interpreter.exceptions.BreakException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;
import interpreter.types.Value;
import interpreter.utilities.Environment;
import interpreter.utilities.Iterator;
import interpreter.utilities.IteratorExecutionContainer;

import java.util.List;

public class Exists extends Expr {
    private Iterator mIterator;
    private BoolExpr mBoolExpr;

    private class Exec implements IteratorExecutionContainer {
        private BoolExpr    mBoolExpr;
        public  SetlBoolean mResult;
        public  Environment mEnv;

        public Exec (BoolExpr boolExpr) {
            mBoolExpr = boolExpr;
            mResult   = SetlBoolean.FALSE;
            mEnv      = null;
        }

        public void execute(Value lastIterationValue) throws SetlException {
            mResult = mBoolExpr.eval();
            mEnv    = Environment.getEnv();         // save state
            if (mResult == SetlBoolean.TRUE) {
                mEnv    = Environment.getEnv();     // save state where result is true
                throw new BreakException("exists"); // stop iteration
            }
        }
    }

    public Exists(Iterator iterator, BoolExpr boolExpr) {
        mIterator = iterator;
        mBoolExpr = boolExpr;
    }

    public SetlBoolean evaluate() throws SetlException {
        Exec e = new Exec(mBoolExpr);
        mIterator.eval(e);
        if (e.mResult == SetlBoolean.TRUE && e.mEnv != null) {
            // restore state in which mBoolExpr is true
            Environment.setEnv(e.mEnv);
        }
        return e.mResult;
    }

    public String toString(int tabs) {
        return "exists (" + mIterator.toString(tabs) + " | " + mBoolExpr.toString(tabs) +")";
    }
}

