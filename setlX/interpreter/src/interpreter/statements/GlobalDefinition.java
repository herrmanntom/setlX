package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Variable;
import interpreter.utilities.Environment;

public class GlobalDefinition extends Statement {
    private Variable mVar;

    public GlobalDefinition(Variable var) {
        mVar = var;
    }

    public void execute() throws SetlException {
        mVar.makeGlobal();
    }

    public String toString(int tabs) {
        return Environment.getTabs(tabs) + "var " + mVar.toString(tabs) + ";";
    }
}
