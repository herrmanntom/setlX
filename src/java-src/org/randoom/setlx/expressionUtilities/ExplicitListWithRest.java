package org.randoom.setlx.expressionUtilities;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.Iterator;
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
public class ExplicitListWithRest extends CollectionBuilder {
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(ExplicitListWithRest.class);

    private final List<Expr> list;
    private final Expr       rest;

    /**
     * Create new ExplicitList.
     *
     * @param exprList List of expressions to evaluate.
     * @param rest     Expression to assign the rest to.
     */
    public ExplicitListWithRest(final List<Expr> exprList, final Expr rest) {
        this.list = exprList;
        this.rest = rest;
    }

    @Override
    public void fillCollection(final State state, final CollectionValue collection) throws SetlException {
        for (final Expr e: list) {
            collection.addMember(state, e.eval(state));
        }
        final Value rest = this.rest.eval(state);
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
    public void collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        for (final Expr expr : list) {
            expr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        }
        rest.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb) {
        final Iterator<Expr> iter = list.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(" | ");
        rest.appendString(state, sb, 0);
    }

    /* term operations */

    @Override
    public void addToTerm(final State state, final CollectionValue collection) throws SetlException {
        final Term     result  = new Term(FUNCTIONAL_CHARACTER, 2);

        final SetlList members = new SetlList(list.size());
        for (final Expr member: list) {
            members.addMember(state, member.toTerm(state));
        }
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
            final SetlList   exprs    = (SetlList) term.firstMember();
            final List<Expr> exprList = new ArrayList<Expr>(exprs.size());
            for (final Value v : exprs) {
                exprList.add(TermConverter.valueToExpr(state, v));
            }
            final Expr rest = TermConverter.valueToExpr(state, term.lastMember());
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
    public int compareTo(final CollectionBuilder other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == ExplicitListWithRest.class) {
            ExplicitListWithRest otherExplicitListWithRest = (ExplicitListWithRest) other;
            if (rest == otherExplicitListWithRest.rest && list == otherExplicitListWithRest.list) {
                return 0; // clone
            }
            int cmp = rest.compareTo(otherExplicitListWithRest.rest);
            if (cmp != 0) {
                return cmp;
            }
            final Iterator<Expr> iterFirst  = list.iterator();
            final Iterator<Expr> iterSecond = otherExplicitListWithRest.list.iterator();
            while (iterFirst.hasNext() && iterSecond.hasNext()) {
                cmp = iterFirst.next().compareTo(iterSecond.next());
                if (cmp != 0) {
                    return cmp;
                }
            }
            if (iterFirst.hasNext()) {
                return 1;
            }
            if (iterSecond.hasNext()) {
                return -1;
            }
            return 0;
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
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == ExplicitListWithRest.class) {
            ExplicitListWithRest otherExplicitListWithRest = (ExplicitListWithRest) obj;
            if (rest == otherExplicitListWithRest.rest && list == otherExplicitListWithRest.list) {
                return true; // clone
            } else if (list.size() == otherExplicitListWithRest.list.size() && rest.equals(otherExplicitListWithRest.rest)) {
                final Iterator<Expr> iterFirst  = list.iterator();
                final Iterator<Expr> iterSecond = otherExplicitListWithRest.list.iterator();
                while (iterFirst.hasNext() && iterSecond.hasNext()) {
                    if ( ! iterFirst.next().equals(iterSecond.next())) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
              int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + rest.hashCode();
        final int size = list.size();
        hash = hash * 31 + size;
        if (size >= 1) {
            hash = hash * 31 + list.get(0).hashCode();
            if (size >= 2) {
                hash = hash * 31 + list.get(size-1).hashCode();
            }
        }
        return hash;
    }
}

