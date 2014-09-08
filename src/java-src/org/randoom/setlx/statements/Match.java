package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.statementBranches.MatchAbstractBranch;
import org.randoom.setlx.statementBranches.MatchDefaultBranch;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;
import org.randoom.setlx.utilities.VariableScope;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the match statement.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'match' '(' expr ')' '{' [...] '}'
 *     ;
 *
 * implemented with different classes which inherit from MatchAbstractBranch:
 *                   ====          ====
 *                   expr       branchList
 */
public class Match extends Statement {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Match.class);

    private final Expr                        expr;
    private final List<MatchAbstractBranch>   branchList;

    /**
     * Create a new match statement.
     *
     * @param expr       Expression forming the term to match.
     * @param branchList List of match branches.
     */
    public Match(final Expr expr, final List<MatchAbstractBranch> branchList) {
        this.expr       = expr;
        this.branchList = branchList;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        final Value         term       = expr.eval(state).toTerm(state);
        final VariableScope outerScope = state.getScope();
        try {
            // increase callStackDepth
            ++(state.callStackDepth);

            for (final MatchAbstractBranch br : branchList) {
                final MatchResult result = br.matches(state, term);
                if (result.isMatch()) {
                    // scope for execution
                    final VariableScope innerScope = outerScope.createInteratorBlock();
                    state.setScope(innerScope);

                    // force match variables to be local to this block
                    final int writeThroughToken = innerScope.unsetWriteThrough();
                    // put all matching variables into current scope
                    result.setAllBindings(state, FUNCTIONAL_CHARACTER);
                    // reset WriteThrough, because changes during execution are not strictly local
                    innerScope.setWriteThrough(writeThroughToken);

                    if (br.evalConditionToBool(state)) {
                        // execute statements
                        final ReturnMessage execResult = br.getStatements().execute(state);

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
        } catch (final StackOverflowError soe) {
            state.storeStackDepthOfFirstCall(state.callStackDepth);
            throw soe;
        } finally {
            // decrease callStackDepth
            --(state.callStackDepth);
            // make sure scope is always reset
            state.setScope(outerScope);
        }
    }

    @Override
    public void collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        expr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);

        // binding inside an match are only valid if present in all branches
        // and last branch is an default-branch
        final int preBound = boundVariables.size();
        List<String> boundHere = null;
        for (final MatchAbstractBranch br : branchList) {
            final List<String> boundTmp = new ArrayList<String>(boundVariables);

            br.collectVariablesAndOptimize(state, boundTmp, unboundVariables, usedVariables);

            if (boundHere == null) {
                boundHere = new ArrayList<String>(boundTmp.subList(preBound, boundTmp.size()));
            } else {
                boundHere.retainAll(boundTmp.subList(preBound, boundTmp.size()));
            }
        }
        if (branchList.get(branchList.size() - 1) instanceof MatchDefaultBranch) {
            boundVariables.addAll(boundHere);
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        sb.append("match (");
        expr.appendString(state, sb, 0);
        sb.append(") {");
        sb.append(state.getEndl());
        for (final MatchAbstractBranch br : branchList) {
            br.appendString(state, sb, tabs + 1);
        }
        state.appendLineStart(sb, tabs);
        sb.append("}");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        result.addMember(state, expr.toTerm(state));

        final SetlList branchList = new SetlList(this.branchList.size());
        for (final MatchAbstractBranch br: this.branchList) {
            branchList.addMember(state, br.toTerm(state));
        }
        result.addMember(state, branchList);

        return result;
    }

    /**
     * Convert a term representing a Match statement into such a statement.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting Match Statement.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static Match termToStatement(final State state, final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.lastMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr                      expr       = TermConverter.valueToExpr(state, term.firstMember());
            final SetlList                  branches   = (SetlList) term.lastMember();
            final List<MatchAbstractBranch> branchList = new ArrayList<MatchAbstractBranch>(branches.size());
            for (final Value v : branches) {
                branchList.add(MatchAbstractBranch.valueToMatchAbstractBranch(state, v));
            }
            return new Match(expr, branchList);
        }
    }
}

