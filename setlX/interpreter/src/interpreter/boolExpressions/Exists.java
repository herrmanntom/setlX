package interpreter.boolExpressions;

import interpreter.exceptions.BreakException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;
import interpreter.types.Value;
import interpreter.utilities.Condition;
import interpreter.utilities.Environment;
import interpreter.utilities.Iterator;
import interpreter.utilities.IteratorExecutionContainer;

import java.util.List;

public class Exists extends Expr {
    private Iterator  mIterator;
    private Condition mCondition;

    private class Exec implements IteratorExecutionContainer {
        private Condition   mCondition;
        public  SetlBoolean mResult;
        public  Environment mEnv;

        public Exec (Condition condition) {
            mCondition = condition;
            mResult    = SetlBoolean.FALSE;
            mEnv       = null;
        }

        public void execute(Value lastIterationValue) throws SetlException {
            mResult = mCondition.eval();
            if (mResult == SetlBoolean.TRUE) {
                mEnv = Environment.getEnv();        // save state where result is true
                throw new BreakException("exists"); // stop iteration
            }
        }
    }

    public Exists(Iterator iterator, Condition condition) {
        mIterator  = iterator;
        mCondition = condition;
    }

    public SetlBoolean evaluate() throws SetlException {
        Exec e = new Exec(mCondition);
        mIterator.eval(e);
        if (e.mResult == SetlBoolean.TRUE && e.mEnv != null) {
            // restore state in which mCondition is true
            Environment.setEnv(e.mEnv);
        }
        return e.mResult;
    }

    public String toString(int tabs) {
        return "exists (" + mIterator.toString(tabs) + " | " + mCondition.toString(tabs) +")";
    }
}

