package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.TermConverter;
import org.randoom.setlx.utilities.VariableScope;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

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
        final VariableScope outerScope = VariableScope.getScope();
        try {
            SetlString string = (SetlString) value.clone();
            while(string.size() > 0) {
                int                     largestMatchSize   = Integer.MIN_VALUE;
                MatchAbstractScanBranch largestMatchBranch = null;
                MatchResult             largestMatchResult = null;
                // find branch which matches largest string
                for (final MatchAbstractScanBranch br : mBranchList) {
                    final MatchResult result = br.scannes(string);
                    if (result.isMatch()) {
                        final int offset = br.getEndOffset();
                        if (offset > largestMatchSize) {
                            // scope for condition
                            final VariableScope innerScope = outerScope.clone();
                            VariableScope.setScope(innerScope);

                            // put all matching variables into scope
                            result.setAllBindings();

                            // check conditon
                            if (br.evalConditionToBool()) {
                                largestMatchSize   = offset;
                                largestMatchBranch = br;
                                largestMatchResult = result;
                            }

                            // reset scope
                            VariableScope.setScope(outerScope);
                        }
                    }
                }
                // execute branch which matches largest string
                if (largestMatchBranch != null && largestMatchResult != null) {
                    if (largestMatchSize == MatchDefaultBranch.END_OFFSET) {
                        // default branch was largest match, stop scan after its execution
                        return largestMatchBranch.execute();
                    }

                    // scope for execution
                    final VariableScope innerScope = outerScope.createInteratorBlock();
                    VariableScope.setScope(innerScope);

                    // force match variables to be local to this block
                    innerScope.setWriteThrough(false);
                    // put all matching variables into current scope
                    largestMatchResult.setAllBindings();
                    // reset WriteThrough, because changes during execution are not strictly local
                    innerScope.setWriteThrough(true);

                    // execute statements
                    final Value execResult = largestMatchBranch.execute();

                    // reset scope
                    VariableScope.setScope(outerScope);

                    if (execResult != null) {
                        return execResult;
                    }

                    // reduce scanned string
                    string = string.getMembers(largestMatchSize + 1, string.size());
                } else {
                    // nothing matched!
                    throw new UndefinedOperationException("Infinite loop in scan-statement detected.");
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

