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

public class Forall extends Expr {
    private Iterator mIterator;
    private BoolExpr mBoolExpr;

    private class Exec implements IteratorExecutionContainer {
        private BoolExpr    mBoolExpr;
        public  SetlBoolean mResult;
//        public  Environment mEnv;

        public Exec (BoolExpr boolExpr) {
            mBoolExpr = boolExpr;
            mResult   = SetlBoolean.TRUE;
//            mEnv      = null;
        }

        public void execute(Value lastIterationValue) throws SetlException {
            mResult = mBoolExpr.eval();
            if (mResult == SetlBoolean.FALSE) {
//                mEnv = Environment.getEnv();       // save state in which mBoolExpr is false
                throw new ExitException("forall"); // stop iteration
            }
        }
    }

    public Forall(Iterator iterator, BoolExpr boolExpr) {
        mIterator = iterator;
        mBoolExpr = boolExpr;
    }

    public SetlBoolean evaluate() throws SetlException {
        Exec e = new Exec(mBoolExpr);
        mIterator.eval(e);
// this is how it should work
//        if (e.mResult == SetlBoolean.FALSE) {
//            // retore state in which mBoolExpr is false
//            Environment.setEnv(e.mEnv);
//        }

        return e.mResult;
    }

    public String toString() {
        return "forall " + mIterator + " | " + mBoolExpr;
    }
}
