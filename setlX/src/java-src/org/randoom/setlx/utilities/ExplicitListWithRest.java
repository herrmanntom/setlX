package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;

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

public class ExplicitListWithRest extends Constructor {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = "^explicitListWithRest";

    private final List<Expr> mList;
    private final Expr       mRest;

    public ExplicitListWithRest(final List<Expr> exprList, final Expr rest) {
        mList = exprList;
        mRest = rest;
    }

    public void fillCollection(final CollectionValue collection) throws SetlException {
        for (final Expr e: mList) {
            collection.addMember(e.eval());
        }
        final Value rest = mRest.eval();
        if (rest instanceof CollectionValue) {
            for (final Value v: (CollectionValue) rest) {
                collection.addMember(v);
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
    public void collectVariablesAndOptimize (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        for (final Expr expr : mList) {
            expr.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
        mRest.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    public void appendString(final StringBuilder sb) {
        final Iterator<Expr> iter = mList.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(" | ");
        mRest.appendString(sb, 0);
    }

    /* term operations */

    public void addToTerm(final CollectionValue collection) {
        final Term     result  = new Term(FUNCTIONAL_CHARACTER, 2);

        final SetlList members = new SetlList(mList.size());
        for (final Expr member: mList) {
            members.addMember(member.toTerm());
        }
        result.addMember(members);

        result.addMember(mRest.toTerm());

        collection.addMember(result);
    }

    public static MatchResult matchTerm(final Term elwRTerm, final CollectionValue collection) throws IncompatibleTypeException {
        final String fc = elwRTerm.functionalCharacter().getUnquotedString();
        if (fc.equals(FUNCTIONAL_CHARACTER) && elwRTerm.size() == 2 && elwRTerm.firstMember() instanceof SetlList) {
            final SetlList terms = (SetlList) elwRTerm.firstMember();
            if (collection.size() >= terms.size()) {
                final CollectionValue other  = (CollectionValue) collection.clone();
                final MatchResult     result = new MatchResult(true);
                for (final Value term : terms) {
                    final MatchResult subResult = term.matchesTerm(other.removeFirstMember());
                    if (subResult.isMatch() && result.isMatch()) {
                        result.addBindings(subResult);
                    } else {
                        return new MatchResult(false);
                    }
                }
                final MatchResult subResult = elwRTerm.lastMember().matchesTerm(other);
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

