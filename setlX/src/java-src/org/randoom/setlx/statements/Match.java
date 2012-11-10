package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;
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

    protected Value exec(final State state) throws SetlException {
        final Value term = mExpr.eval(state).toTerm(state);
        final VariableScope outerScope = state.getScope();
        try {
            for (final MatchAbstractBranch br : mBranchList) {
                final MatchResult result = br.matches(state, term);
                if (result.isMatch()) {
                    // scope for execution
                    final VariableScope innerScope = outerScope.createInteratorBlock();
                    state.setScope(innerScope);

                    // force match variables to be local to this block
                    innerScope.setWriteThrough(false);
                    // put all matching variables into current scope
                    result.setAllBindings(state);
                    // reset WriteThrough, because changes during execution are not strictly local
                    innerScope.setWriteThrough(true);

                    if (br.evalConditionToBool(state)) {
                        // execute statements
                        final Value execResult = br.execute(state);

                        // reset scope
                        state.setScope(outerScope);

                        if (execResult != null) {
                            return execResult;
                        }

                        break;
                    } else {
                        // reset scope
                        state.setScope(outerScope);
                    }
                }
            }
            return null;
        } finally { // make sure scope is always reset
            state.setScope(outerScope);
        }
    }

    /* Gather all bound and unbound variables in this statement and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       Optimize sub-expressions during this process by calling optimizeAndCollectVariables()
       when adding variables from them.
    */
    public void collectVariablesAndOptimize (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        mExpr.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);

        // binding inside an match are only valid if present in all branches
        // and last branch is an default-branch
        final int      preBound  = boundVariables.size();
        List<Variable> boundHere = null;
        for (final MatchAbstractBranch br : mBranchList) {
            final List<Variable> boundTmp = new ArrayList<Variable>(boundVariables);

            br.collectVariablesAndOptimize(boundTmp, unboundVariables, usedVariables);

            if (boundHere == null) {
                boundHere = new ArrayList<Variable>(boundTmp.subList(preBound, boundTmp.size()));
            } else {
                boundHere.retainAll(boundTmp.subList(preBound, boundTmp.size()));
            }
        }
        if (mBranchList.get(mBranchList.size() - 1) instanceof MatchDefaultBranch) {
            boundVariables.addAll(boundHere);
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

    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        result.addMember(mExpr.toTerm(state));

        final SetlList branchList = new SetlList(mBranchList.size());
        for (final MatchAbstractBranch br: mBranchList) {
            branchList.addMember(br.toTerm(state));
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

