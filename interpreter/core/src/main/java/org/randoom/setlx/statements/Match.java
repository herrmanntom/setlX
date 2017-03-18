package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.statementBranches.AbstractMatchBranch;
import org.randoom.setlx.statementBranches.MatchDefaultBranch;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.*;

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
 * implemented with different classes which inherit from AbstractMatchBranch:
 *                   ====          ====
 *                   expr       branchList
 */
public class Match extends Statement {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(Match.class);

    private final OperatorExpression expr;
    private final FragmentList<AbstractMatchBranch> branchList;

    /**
     * Create a new match statement.
     *
     * @param expr       Expression forming the term to match.
     * @param branchList List of match branches.
     */
    public Match(final OperatorExpression expr, final FragmentList<AbstractMatchBranch> branchList) {
        this.expr       = expr;
        this.branchList = branchList;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        final Value         term       = expr.evaluate(state).toTerm(state);
        final VariableScope outerScope = state.getScope();
        try {
            for (final AbstractMatchBranch br : branchList) {
                final MatchResult result = br.matches(state, term);
                if (result.isMatch()) {
                    // scope for execution
                    final VariableScope innerScope = outerScope.createIteratorBlock();
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
        } finally {
            // make sure scope is always reset
            state.setScope(outerScope);
        }
    }

    @Override
    public boolean collectVariablesAndOptimize (
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
        for (final AbstractMatchBranch br : branchList) {
            final List<String> boundTmp = new ArrayList<String>(boundVariables);

            br.collectVariablesAndOptimize(state, boundTmp, unboundVariables, usedVariables);

            if (boundHere == null) {
                boundHere = new ArrayList<String>(boundTmp.subList(preBound, boundTmp.size()));
            } else {
                boundHere.retainAll(boundTmp.subList(preBound, boundTmp.size()));
            }
        }
        if (boundHere != null && branchList.get(branchList.size() - 1) instanceof MatchDefaultBranch) {
            boundVariables.addAll(boundHere);
        }
        return false;
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        sb.append("match (");
        expr.appendString(state, sb, 0);
        sb.append(") {");
        sb.append(state.getEndl());
        for (final AbstractMatchBranch br : branchList) {
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
        for (final AbstractMatchBranch br: this.branchList) {
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
            final OperatorExpression                expr       = OperatorExpression.createFromTerm(state, term.firstMember());
            final SetlList                          branches   = (SetlList) term.lastMember();
            final FragmentList<AbstractMatchBranch> branchList = new FragmentList<AbstractMatchBranch>(branches.size());
            for (final Value v : branches) {
                branchList.add(AbstractMatchBranch.valueToMatchAbstractBranch(state, v));
            }
            return new Match(expr, branchList);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Match.class) {
            Match otr = (Match) other;
            final int cmp = expr.compareTo(otr.expr);
            if (cmp != 0) {
                return cmp;
            }
            return branchList.compareTo(otr.branchList);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Match.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == Match.class) {
            Match otr = (Match) obj;
            return expr.equals(otr.expr) && branchList.equals(otr.branchList);
        }
        return false;
    }

    @Override
    public final int computeHashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + expr.hashCode();
        hash = hash * 31 + branchList.hashCode();
        return hash;
    }
}

