package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlList;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.Condition;
import interpreter.utilities.Environment;
import interpreter.utilities.MatchResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'match' '(' expr ')' '{' ('case' exprList ':' block)* ('default' ':' block)? '}'
    ;

implemented here as:
                                       ========     =====
                                        mTerms   mStatements
*/

public class BranchMatch extends BranchMatchAbstract {
    private List<Expr>  mExprs;      // expressions which creates terms to match
    private List<Value> mTerms;      // terms to match
    private Block       mStatements; // block to execute after match

    public BranchMatch(List<Expr> exprs, Block statements){
        mExprs      = exprs;
        mTerms      = new ArrayList<Value>(exprs.size());
        for (Expr expr: exprs) {
            mTerms.add(expr.toTerm());
        }
        mStatements = statements;
    }

    public MatchResult matches(Value term) {
        MatchResult last = new MatchResult(false);
        for (Value v : mTerms) {
            last = v.matchesTerm(term);
            if (last.isMatch()) {
                return last;
            }
        }
        return last;
    }

    public void execute() throws SetlException {
        mStatements.execute();
    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs);
        result += "case ";

        Iterator<Expr> iter = mExprs.iterator();
        while (iter.hasNext()) {
            Expr expr   = iter.next();
            result += expr.toString(tabs);
            if (iter.hasNext()) {
                result += ", ";
            }
        }

        result += ":" + Environment.getEndl();
        result += mStatements.toString(tabs + 1) + Environment.getEndl();
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term     result   = new Term("'match");

        SetlList termList = new SetlList();
        for (Value v: mTerms) {
            termList.addMember(v);
        }
        result.addMember(termList);
        result.addMember(mStatements.toTerm());
        return result;
    }
}

