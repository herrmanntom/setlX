package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.TermConverter;
import org.randoom.setlx.utilities.VariableScope;

import java.util.ArrayList;
import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'match' '(' expr ')' '{' [...] '}'
    ;

implemented with different classes which inherit from MatchAbstractBranch:
                  ====          ====
                  mExpr      mBranchList
*/

public class Match extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^match";

    private final Expr                        mExpr;
    private final List<MatchAbstractBranch>   mBranchList;

    public Match(final Expr expr, final List<MatchAbstractBranch> branchList) {
        mExpr       = expr;
        mBranchList = branchList;
    }

    protected Value exec() throws SetlException {
        final Value term = mExpr.eval().toTerm();
        final VariableScope outerScope = VariableScope.getScope();
        try {
            for (final MatchAbstractBranch br : mBranchList) {
                final MatchResult result = br.matches(term);
                if (result.isMatch()) {
                    // scope for execution
                    final VariableScope innerScope = outerScope.createInteratorBlock();
                    VariableScope.setScope(innerScope);

                    // force match variables to be local to this block
                    innerScope.setWriteThrough(false);
                    // put all matching variables into current scope
                    result.setAllBindings();
                    // reset WriteThrough, because changes during execution are not strictly local
                    innerScope.setWriteThrough(true);

                    if (br.evalConditionToBool()) {
                        // execute statements
                        final Value execResult = br.execute();

                        // reset scope
                        VariableScope.setScope(outerScope);

                        if (execResult != null) {
                            return execResult;
                        }

                        break;
                    } else {
                        // reset scope
                        VariableScope.setScope(outerScope);
                    }
                }
            }
            return null;
        } finally { // make sure scope is always reset
            VariableScope.setScope(outerScope);
        }
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        Environment.getLineStart(sb, tabs);
        sb.append("match (");
        mExpr.appendString(sb, 0);
        sb.append(") {");
        sb.append(Environment.getEndl());
        for (final MatchAbstractBranch br : mBranchList) {
            br.appendString(sb, tabs + 1);
        }
        Environment.getLineStart(sb, tabs);
        sb.append("}");
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        result.addMember(mExpr.toTerm());

        final SetlList branchList = new SetlList(mBranchList.size());
        for (final MatchAbstractBranch br: mBranchList) {
            branchList.addMember(br.toTerm());
        }
        result.addMember(branchList);

        return result;
    }

    public static Match termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.lastMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr                        expr        = TermConverter.valueToExpr(term.firstMember());
            final SetlList                    branches    = (SetlList) term.lastMember();
            final List<MatchAbstractBranch>   branchList  = new ArrayList<MatchAbstractBranch>(branches.size());
            for (final Value v : branches) {
                branchList.add(MatchAbstractBranch.valueToMatchAbstractBranch(v));
            }
            return new Match(expr, branchList);
        }
    }
}

