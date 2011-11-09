package interpreter.statements;

import interpreter.exceptions.ReturnException;
import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.Om;
import interpreter.utilities.Environment;

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

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs) + "return";
        if (mResult != null){
            result += " " + mResult;
        }
        result += ";";
        return result;
    }
}
