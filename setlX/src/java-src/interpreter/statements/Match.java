package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.types.SetlList;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.Environment;

import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'match' '(' expr ')' '{' ('case' expr ':' block)* ('default' ':' block)? '}'
    ;

implemented with different classes which inherit from BranchMatchAbstract:
                  ====          =====================    ===================
                  mExpr              BranchMatch            BranchDefault
*/

public class Match extends Statement {
    private Expr                        mExpr;
    private List<BranchMatchAbstract>   mBranchList;

    public Match(Expr expr, List<BranchMatchAbstract> branchList) {
        mExpr       = expr;
        mBranchList = branchList;
    }

    public void execute() throws SetlException {
        Value term = mExpr.eval().toTerm();
        for (BranchMatchAbstract b : mBranchList) {
            if (b.matches(term)) {
                b.execute();
                break;
            }
        }
    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs) + "match (" + mExpr.toString(tabs) + ") {" + Environment.getEndl();
        for (BranchMatchAbstract b : mBranchList) {
            result += b.toString(tabs + 1);
        }
        result += Environment.getTabs(tabs) + "}";
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term("'matchBlock");

        result.addMember(mExpr.toTerm());

        SetlList branchList = new SetlList();
        for (BranchMatchAbstract br: mBranchList) {
            branchList.addMember(br.toTerm());
        }
        result.addMember(branchList);

        return result;
    }
}

