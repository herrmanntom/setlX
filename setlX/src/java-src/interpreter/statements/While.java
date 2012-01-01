package interpreter.statements;

import interpreter.exceptions.BreakException;
import interpreter.exceptions.ContinueException;
import interpreter.exceptions.SetlException;
import interpreter.types.Term;
import interpreter.utilities.Condition;
import interpreter.utilities.Environment;

/*
grammar rule:
statement
    : [...]
    | 'while' '(' condition ')' '{' block '}'
    ;

implemented here as:
                  =========         =====
                  mCondition     mStatements
*/

public class While extends Statement {
    private Condition mCondition;
    private Block     mStatements;

    public While(Condition condition, Block statements) {
        mCondition  = condition;
        mStatements = statements;
    }

    public void execute() throws SetlException {
        while (mCondition.evalToBool()) {
            try{
                mStatements.execute();
            } catch (ContinueException e) {
                continue;
            } catch (BreakException e) {
                break;
            }
        }
    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs);
        result += "while (" + mCondition.toString(tabs) + ") ";
        result += mStatements.toString(tabs, true);
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term("'while");
        result.addMember(mCondition.toTerm());
        result.addMember(mStatements.toTerm());
        return result;
    }
}

