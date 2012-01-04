package interpreter.boolExpressions;

import interpreter.exceptions.BreakException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlBoolean;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.Condition;
import interpreter.utilities.Iterator;
import interpreter.utilities.IteratorExecutionContainer;
import interpreter.utilities.VariableScope;

import java.util.List;

/*
grammar rule:
boolExpr
    : 'forall' '(' iteratorChain '|' condition ')'
    | [...]
    ;

implemented here as:
                   ========-----     =========
                     mIterator       mCondition
*/

public class Forall extends Expr {
    private Iterator  mIterator;
    private Condition mCondition;

    private class Exec implements IteratorExecutionContainer {
        private Condition       mCondition;
        public  SetlBoolean     mResult;
        public  VariableScope   mScope;

        public Exec (Condition condition) {
            mCondition = condition;
            mResult    = SetlBoolean.TRUE;
            mScope     = null;
        }

        public void execute(Value lastIterationValue) throws SetlException {
            mResult = mCondition.eval();
            if (mResult == SetlBoolean.FALSE) {
                mScope = VariableScope.getScope();  // save state where result is true
                throw new BreakException("forall"); // stop iteration
            }
        }
    }

    public Forall(Iterator iterator, Condition condition) {
        mIterator  = iterator;
        mCondition = condition;
    }

    public SetlBoolean evaluate() throws SetlException {
        Exec e = new Exec(mCondition);
        mIterator.eval(e);
        if (e.mResult == SetlBoolean.FALSE && e.mScope != null) {
            // retore state in which mBoolExpr is false
            VariableScope.setScope(e.mScope);
        }
        return e.mResult;
    }

    /* string operations */

    public String toString(int tabs) {
        return "forall (" + mIterator.toString(tabs) + " | " + mCondition.toString(tabs) + ")";
    }

    /* term operations */

    public Term toTerm() throws SetlException {
        Term result = new Term("'forall");
        result.addMember(mIterator.toTerm());
        result.addMember(mCondition.toTerm());
        return result;
    }
}

