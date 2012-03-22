package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.expressions.Expr;
import interpreter.types.SetlList;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.Environment;
import interpreter.utilities.MatchResult;
import interpreter.utilities.TermConverter;

import java.util.ArrayList;
import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'match' '(' expr ')' '{' ('case' exprList ':' block)* ('default' ':' block)? '}'
    ;

implemented with different classes which inherit from BranchMatchAbstract:
                  ====          =========================    ===================
                  mExpr              MatchCaseBranch         MatchDefaultBranch
*/

public class Match extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "'match";

    private Expr                        mExpr;
    private List<MatchAbstractBranch>   mBranchList;
    private int                         mLineNr;
    private int                         mLineNr2;

    public Match(Expr expr, List<MatchAbstractBranch> branchList) {
        mExpr       = expr;
        mBranchList = branchList;
        mLineNr     = -1;
        mLineNr2    = -1;
    }

    public int getLineNr() {
        if (mLineNr < 0) {
            computeLineNr();
        }
        return mLineNr;
    }

    public void computeLineNr() {
        mLineNr = ++Environment.sourceLine;
        for (MatchAbstractBranch br : mBranchList) {
            br.computeLineNr();
        }
        mLineNr2 = ++Environment.sourceLine;
    }

    public void execute() throws SetlException {
        Value term = mExpr.eval().toTerm();
        for (MatchAbstractBranch br : mBranchList) {
            MatchResult result = br.matches(term);
            if (result.isMatch()) {
                // put all matching variables into current scope
                result.setAllBindings();
                // execute statements
                br.execute();
                break;
            }
        }
    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getLineStart(getLineNr(), tabs) + "match (" + mExpr.toString(tabs) + ") {" + Environment.getEndl();
        for (MatchAbstractBranch br : mBranchList) {
            result += br.toString(tabs + 1);
        }
        result += Environment.getLineStart(mLineNr2, tabs) + "}";
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);

        result.addMember(mExpr.toTerm());

        SetlList branchList = new SetlList();
        for (MatchAbstractBranch br: mBranchList) {
            branchList.addMember(br.toTerm());
        }
        result.addMember(branchList);

        return result;
    }

    public static Match termToStatement(Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.lastMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr                        expr        = TermConverter.valueToExpr(term.firstMember());
            SetlList                    branches    = (SetlList) term.lastMember();
            List<MatchAbstractBranch>   branchList  = new ArrayList<MatchAbstractBranch>(branches.size());
            for (Value v : branches) {
                branchList.add(MatchAbstractBranch.valueToMatchAbstractBranch(v));
            }
            return new Match(expr, branchList);
        }
    }
}

