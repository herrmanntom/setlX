package interpreter.boolExpressions;

import interpreter.Environment;
import interpreter.exceptions.ExitException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.expressions.Iterator;
import interpreter.expressions.IteratorExecutionContainer;
import interpreter.types.SetlBoolean;
import interpreter.types.Value;

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
            mEnv    = Environment.getEnv();           // save state
            if (mResult == SetlBoolean.TRUE) {
                mEnv    = Environment.getEnv();    // save state where result is true
                throw new ExitException("exists"); // stop iteration
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
// this is how it should work
//        if (e.mResult == SetlBoolean.TRUE) {
//            // restore state in which mBoolExpr is true
        if (e.mEnv != null) {
            Environment.setEnv(e.mEnv); // the 'bug-compatible' way is to always restore
        }

        return e.mResult;
    }

    public String toString() {
        return "exists " + mIterator + " | " + mBoolExpr;
    }
}
