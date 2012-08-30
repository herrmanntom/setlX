package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'scan' '(' expr ')' '{' [...] '}'
    ;

implemented with different classes which inherit from MatchAbstractScanBranch:
                  ====        =====
                  mExpr    mBranchList
*/

public class Scan extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^scan";

    private final Expr                          mExpr;
    private final List<MatchAbstractScanBranch> mBranchList;

    public Scan(final Expr expr, final List<MatchAbstractScanBranch> branchList) {
        mExpr       = expr;
        mBranchList = branchList;
    }

    protected Value exec() throws SetlException {
        final Value value = mExpr.eval();
        if ( ! (value instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "The value '" + value + "' is not a string and cannot be scaned."
            );
        }
        Value      execResult = null;
        SetlString string     = (SetlString) value.clone();
        while(string.size() > 0) {
            for (final MatchAbstractScanBranch br : mBranchList) {
                final MatchResult result = br.scannes(string);
                if (result.isMatch()) {
                    // put all matching variables into current scope
                    result.setAllBindings();
                    if (br.evalConditionToBool()) {
                        // reduce string to scan
                        final int offset = br.getEndOffset();
                        if (offset >= 0) {
                            string = string.getMembers(offset + 1, string.size());
                        }
                        // execute statements
                        execResult = br.execute();
                        if (execResult != null) {
                            return execResult;
                        } else if (offset == MatchDefaultBranch.END_OFFSET) {
                            return null;
                        }
                        result.restoreAllBindings();
                        break;
                    }
                    result.restoreAllBindings();
                }
            }
        }
        return null;
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        Environment.getLineStart(sb, tabs);
        sb.append("scan (");
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

    public static Scan termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.lastMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr                          expr        = TermConverter.valueToExpr(term.firstMember());
            final SetlList                      branches    = (SetlList) term.lastMember();
            final List<MatchAbstractScanBranch> branchList  = new ArrayList<MatchAbstractScanBranch>(branches.size());
            for (final Value v : branches) {
                branchList.add(MatchAbstractScanBranch.valueToMatchAbstractScanBranch(v));
            }
            return new Scan(expr, branchList);
        }
    }
}

