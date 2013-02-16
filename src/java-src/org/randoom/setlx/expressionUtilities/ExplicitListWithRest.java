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

/*
grammar rule:
explicitList
    : anyExpr (',' anyExpr)* '|' expr
    ;

implemented here as:
      =======......=======       ====
             mList               mRest
*/

public class ExplicitListWithRest extends CollectionBuilder {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = "^explicitListWithRest";

    private final List<Expr> mList;
    private final Expr       mRest;

    public ExplicitListWithRest(final List<Expr> exprList, final Expr rest) {
        mList = exprList;
        mRest = rest;
    }

    @Override
    public void fillCollection(final State state, final CollectionValue collection) throws SetlException {
        for (final Expr e: mList) {
            collection.addMember(state, e.eval(state));
        }
        final Value rest = mRest.eval(state);
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

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        for (final Expr expr : mList) {
            expr.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
        mRest.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb) {
        final Iterator<Expr> iter = mList.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(" | ");
        mRest.appendString(state, sb, 0);
    }

    /* term operations */

    @Override
    public void addToTerm(final State state, final CollectionValue collection) {
        final Term     result  = new Term(FUNCTIONAL_CHARACTER, 2);

        final SetlList members = new SetlList(mList.size());
        for (final Expr member: mList) {
            members.addMember(state, member.toTerm(state));
        }
        result.addMember(state, members);

        result.addMember(state, mRest.toTerm(state));

        collection.addMember(state, result);
    }

    public static MatchResult matchTerm(final State state, final Term elwRTerm, final CollectionValue collection) throws SetlException {
        final String fc = elwRTerm.functionalCharacter().getUnquotedString();
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

    /*package*/ static ExplicitListWithRest termToExplicitListWithRest(final Term term) throws TermConversionException {
        final String fc = term.functionalCharacter().getUnquotedString();
        if ( ! fc.equals(FUNCTIONAL_CHARACTER) || term.size() != 2 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList   exprs    = (SetlList) term.firstMember();
            final List<Expr> exprList = new ArrayList<Expr>(exprs.size());
            for (final Value v : exprs) {
                exprList.add(TermConverter.valueToExpr(v));
            }
            final Expr rest = TermConverter.valueToExpr(term.lastMember());
            return new ExplicitListWithRest(exprList, rest);
        }
    }
}

