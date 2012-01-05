package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.Condition;
import interpreter.utilities.Environment;
import interpreter.utilities.MatchResult;

/*
grammar rule:
statement
    : [...]
    | 'match' '(' expr ')' '{' ('case' expr ':' block)* ('default' ':' block)? '}'
    ;

implemented here as:
                                       ====     =====
                                       mTerm mStatements
*/

public class BranchMatch extends BranchMatchAbstract {
    private Expr    mExpr;       // expr which creates term to match
    private Value   mTerm;       // term to match
    private Block   mStatements; // block to execute after match

    public BranchMatch(Expr expr, Block statements){
        mExpr       = expr;
        mTerm       = expr.toTerm();
        mStatements = statements;
    }

    public MatchResult matches(Value term) {
        return mTerm.matchesTerm(term);
    }

    public void execute() throws SetlException {
        mStatements.execute();
    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs);
        result += "case " + mExpr.toString(tabs) + ":" + Environment.getEndl();
        result += mStatements.toString(tabs + 1) + Environment.getEndl();
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term("'match");
        result.addMember(mTerm);
        result.addMember(mStatements.toTerm());
        return result;
    }
}

