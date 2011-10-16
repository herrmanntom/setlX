package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.utilities.Environment;

public class GlobalDefinition extends Statement {
    private String mId;

    public GlobalDefinition(String id) {
        mId = id;
    }

    public void execute() throws SetlException {
        Environment.makeGlobal(mId);
    }

    public String toString(int tabs) {
        return Environment.getTabs(tabs) + "var " + mId + ";";
    }
}
