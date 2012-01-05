package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.types.Term;

/*
grammar rule:
statement
    : [...]
    | 'if' '(' condition ')' '{' block '}' ('else' 'if' '(' condition ')' '{' block '}')* ('else' '{' block '}')?
    ;

implemented here as:
                                                                                                      =====
                                                                                                   mStatements
*/

public class BranchElse extends BranchAbstract {
    private Block       mStatements;

    public BranchElse(Block statements){
        mStatements = statements;
    }

    public boolean evalConditionToBool() {
        return true;
    }

    public void execute() throws SetlException {
        mStatements.execute();
    }

    /* string operations */

    public String toString(int tabs) {
        String result = " else ";
        result += mStatements.toString(tabs, true);
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term("'else");
        result.addMember(mStatements.toTerm());
        return result;
    }
}

