package interpreter.statements;

import interpreter.Environment;
import interpreter.exceptions.ReturnException;
import interpreter.expressions.Expr;
import interpreter.types.SetlOm;

public class Return extends Statement {
    private Expr mResult;

    public Return(Expr result) {
        mResult = result;
    }

    public void execute() throws ReturnException {
        if (mResult != null) {
            throw new ReturnException(mResult.eval());
        } else {
            throw new ReturnException(SetlOm.OM);
        }
    }

    public String toString(int tabs) {
        if (mResult == null){
            return Environment.getTabs(tabs) + "return;";
        } else {
            return Environment.getTabs(tabs) + "return " + mResult + ";";
        }
    }
}
