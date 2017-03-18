package org.randoom.setlx.operatorUtilities;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.FragmentList;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

import java.util.List;

/**
 * An explicit list of expressions plus rest, used to fill collections.
 *
 * grammar rule:
 * explicitList
 *     : anyExpr (',' anyExpr)* '|' expr
 *     ;
 *
 * implemented here as:
 *       =======......=======       ====
 *               list               rest
 */
public class ExplicitListWithRest extends ExplicitList {
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(ExplicitListWithRest.class);

    private final OperatorExpression rest;

    /**
     * Create new ExplicitList.
     *
     * @param exprList List of expressions to evaluate.
     * @param rest     Expression to assign the rest to.
     */
    public ExplicitListWithRest(final FragmentList<OperatorExpression> exprList, final OperatorExpression rest) {
        super(exprList);
        this.rest = rest;
    }

    @Override
    public void fillCollection(final State state, final CollectionValue collection) throws SetlException {
        super.fillCollection(state, collection);
        final Value rest = this.rest.evaluate(state);
        if (rest instanceof CollectionValue) {
            for (final Value v: (CollectionValue) rest) {
                collection.addMember(state, v);
            }
        } else {
            throw new IncompatibleTypeException(
                "Rest-Argument '" + rest + "' is not a collection value."
            );
        }
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        boolean a = super.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        boolean b = rest.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        return a && b;
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb) {
        super.appendString(state, sb);
        sb.append(" | ");
        rest.appendString(state, sb, 0);
    }

    /* term operations */

    @Override
    public void addToTerm(final State state, final CollectionValue collection) throws SetlException {
        final Term     result  = new Term(FUNCTIONAL_CHARACTER, 2);

        final SetlList members = new SetlList();
        super.addToTerm(state, members);
        result.addMember(state, members);

        result.addMember(state, rest.toTerm(state));

        collection.addMember(state, result);
    }

    /**
     * Match collection value against a term representing an ExplicitListWithRest.
     *
     * @param state          Current state of the running setlX program.
     * @param elwRTerm       Term representing an ExplicitListWithRest.
     * @param collection     Collection to match.
     * @return               Result of the match.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public static MatchResult matchTerm(final State state, final Term elwRTerm, final CollectionValue collection) throws SetlException {
        final String fc = elwRTerm.getFunctionalCharacter();
        if (fc.equals(FUNCTIONAL_CHARACTER) && elwRTerm.size() == 2 && elwRTerm.firstMember() instanceof SetlList) {
            final SetlList terms = (SetlList) elwRTerm.firstMember();
            if (collection.size() >= terms.size()) {
                final CollectionValue other  = (CollectionValue) collection.clone();
                final MatchResult     result = new MatchResult(true);
                for (final Value term : terms) {
                    final MatchResult subResult = term.matchesTerm(state, other.removeFirstMember());
                    if (subResult.isMatch() && result.isMatch()) {
                        result.addBindings(subResult);
                    } else {
                        return new MatchResult(false);
                    }
                }
                final MatchResult subResult = elwRTerm.lastMember().matchesTerm(state, other);
                if (subResult.isMatch()) {
                    result.addBindings(subResult);
                    return result;
                }
            }

        }
        return new MatchResult(false);
    }

    /**
     * Regenerate ExplicitListWithRest from a term representing this expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term representation.
     * @return                         Regenerated ExplicitListWithRest.
     * @throws TermConversionException Thrown in case the term is malformed.
     */
    /*package*/ static ExplicitListWithRest termToExplicitListWithRest(final State state, final Term term) throws TermConversionException {
        final String fc = term.getFunctionalCharacter();
        if ( ! fc.equals(FUNCTIONAL_CHARACTER) || term.size() != 2 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList exprs = (SetlList) term.firstMember();
            final FragmentList<OperatorExpression> exprList = new FragmentList<>(exprs.size());
            for (final Value v : exprs) {
                exprList.add(OperatorExpression.createFromTerm(state, v));
            }
            final OperatorExpression rest = OperatorExpression.createFromTerm(state, term.lastMember());
            return new ExplicitListWithRest(exprList, rest);
        }
    }

    /**
     * Get the functional character used in terms.
     *
     * @return functional character used in terms.
     */
    /*package*/ static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other, boolean ordered) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == ExplicitListWithRest.class) {
            ExplicitListWithRest otherExplicitListWithRest = (ExplicitListWithRest) other;
            int cmp = rest.compareTo(otherExplicitListWithRest.rest);
            if (cmp != 0) {
                return cmp;
            }
            return super.compareTo(otherExplicitListWithRest, ordered);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(ExplicitListWithRest.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(final Object obj, boolean ordered) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == ExplicitListWithRest.class) {
            ExplicitListWithRest otherExplicitListWithRest = (ExplicitListWithRest) obj;
            return rest.equals(otherExplicitListWithRest.rest) && super.equals(otherExplicitListWithRest, ordered);
        }
        return false;
    }

    @Override
    public int computeHashCode(boolean ordered) {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + rest.hashCode();
        return hash * 31 + super.computeHashCode(ordered);
    }
}

