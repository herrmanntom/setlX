package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Variable;
import interpreter.types.Term;
import interpreter.utilities.Environment;

/*
grammar rule:
statement
    : [...]
    | 'var' variable ';'
    ;

implemented here as:
            ========
              mVar
*/

public class GlobalDefinition extends Statement {
    private Variable mVar;

    public GlobalDefinition(Variable var) {
        mVar = var;
    }

    public void execute() throws SetlException {
        mVar.makeGlobal();
    }

    /* string operations */

    public String toString(int tabs) {
        return Environment.getTabs(tabs) + "var " + mVar.toString(tabs) + ";";
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term("'globalDefinition");
        result.addMember(mVar.toTerm());
        return result;
    }
}

