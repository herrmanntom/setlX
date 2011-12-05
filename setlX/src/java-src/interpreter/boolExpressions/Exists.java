package interpreter.boolExpressions;

import interpreter.exceptions.BreakException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;
import interpreter.types.Value;
import interpreter.utilities.Condition;
import interpreter.utilities.Iterator;
import interpreter.utilities.IteratorExecutionContainer;
import interpreter.utilities.VariableScope;

import java.util.List;

/*
grammar rule:
boolExpr
    : 'exists' '(' iterator '|' condition ')'
    | [...]
    ;

implemented here as:
                   ========     =========
                   mIterator    mCondition
*/

public class Exists extends Expr {
    private Iterator  mIterator;
    private Condition mCondition;

    private class Exec implements IteratorExecutionContainer {
        private Condition       mCondition;
        public  SetlBoolean     mResult;
        public  VariableScope   mScope;

        public Exec (Condition condition) {
            mCondition = condition;
            mResult    = SetlBoolean.FALSE;
            mScope     = null;
        }

        public void execute(Value lastIterationValue) throws SetlException {
            mResult = mCondition.eval();
            if (mResult == SetlBoolean.TRUE) {
                mScope = VariableScope.getScope();  // save state where result is true
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
        if (e.mResult == SetlBoolean.TRUE && e.mScope != null) {
            // restore state in which mCondition is true
            VariableScope.setScope(e.mScope);
        }
        return e.mResult;
    }

    public String toString(int tabs) {
        return "exists (" + mIterator.toString(tabs) + " | " + mCondition.toString(tabs) +")";
    }
}

