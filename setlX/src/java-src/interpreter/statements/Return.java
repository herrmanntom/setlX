package interpreter.statements;

import interpreter.exceptions.ReturnException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.Om;
import interpreter.types.SetlString;
import interpreter.types.Term;
import interpreter.utilities.Environment;

/*
grammar rule:
statement
    : [...]
    | 'return' anyExpr? ';'
    ;

implemented here as:
               =======
               mResult
*/

public class Return extends Statement {
    private Expr mResult;

    public Return(Expr result) {
        mResult = result;
    }

    public void execute() throws SetlException {
        if (mResult != null) {
            throw new ReturnException(mResult.eval());
        } else {
            throw new ReturnException(Om.OM);
        }
    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs) + "return";
        if (mResult != null){
            result += " " + mResult;
        }
        result += ";";
        return result;
    }

    /* term operations */

    public Term toTerm() throws SetlException {
        Term result = new Term("'return");
        if (mResult != null) {
            result.addMember(mResult.toTerm());
        } else {
            result.addMember(new SetlString("nil"));
        }
        return result;
    }
}

