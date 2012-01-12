package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.Environment;
import interpreter.utilities.MatchResult;

/*
grammar rule:
statement
    : [...]
    | 'match' '(' expr ')' '{' ('case' exprList ':' block)* ('default' ':' block)? '}'
    ;

implemented here as:
                                                                           =====
                                                                        mStatements
*/

public class BranchMatchDefault extends BranchMatchAbstract {
    private Block       mStatements;

    public BranchMatchDefault(Block statements) {
        mStatements = statements;
    }

    public MatchResult matches(Value term) {
        return new MatchResult(true);
    }

    public void execute() throws SetlException {
        mStatements.execute();
    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs);
        result += "default:" + Environment.getEndl();
        result += mStatements.toString(tabs + 1) + Environment.getEndl();
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term("'matchDefault");
        result.addMember(mStatements.toTerm());
        return result;
    }
}

