package org.randoom.setlx.expressionUtilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressionUtilities.SetlIterator;
import org.randoom.setlx.expressionUtilities.SetlIteratorExecutionContainer;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/*
grammar rules:
shortIterate
    :             iterator       '|' condition
    ;

iterate
    : anyExpr ':' iteratorChain ('|' condition)?
    ;

implemented here as:
      =======     ========-----      =========
       mExpr        mIterator        mCondition
*/

public class SetlIteration extends CollectionBuilder {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = "^setlIteration";

    private final Expr         mExpr;
    private final SetlIterator mIterator;
    private final Condition    mCondition;

    private class Exec implements SetlIteratorExecutionContainer {
        private final Expr            mExpr;
        private final Condition       mCondition;
        private final CollectionValue mCollection;

        public Exec (final Expr expr, final Condition condition, final CollectionValue collection) {
            mExpr       = expr;
            mCondition  = condition;
            mCollection = collection;
        }

        @Override
        public ReturnMessage execute(final State state, final Value lastIterationValue) throws SetlException {
            if (mCondition == null || mCondition.evalToBool(state)) {
                if (mExpr != null) {
                    mCollection.addMember(state, mExpr.eval(state));
                } else { // is simple iteration
                    mCollection.addMember(state, lastIterationValue);
                }
            }
            return null;
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
            if (mCondition != null) {
                mCondition.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
            }
            if (mExpr != null) {
                mExpr.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
            }
        }
    }

    public SetlIteration(final Expr expr, final SetlIterator iterator, final Condition condition) {
        mExpr      = expr;
        mIterator  = iterator;
        mCondition = condition;
    }

    @Override
    public void fillCollection(final State state, final CollectionValue collection) throws SetlException {
        mIterator.eval(state, new Exec(mExpr, mCondition, collection));
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
        mIterator.collectVariablesAndOptimize(new Exec(mExpr, mCondition, null), boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb) {
        if (mExpr != null) {
            mExpr.appendString(state, sb, 0);
            sb.append(" : ");
        }
        mIterator.appendString(state, sb, 0);
        if (mCondition != null) {
            sb.append(" | ");
            mCondition.appendString(state, sb, 0);
        }
    }

    /* term operations */

    @Override
    public void addToTerm(final State state, final CollectionValue collection) {
        final Term result = new Term(FUNCTIONAL_CHARACTER);
        if (mExpr != null) {
            result.addMember(state, mExpr.toTerm(state));
        } else {
            result.addMember(state, new SetlString("nil"));
        }
        result.addMember(state, mIterator.toTerm(state));
        if (mCondition != null) {
            result.addMember(state, mCondition.toTerm(state));
        } else {
            result.addMember(state, new SetlString("nil"));
        }
        collection.addMember(state, result);
    }

    /*package*/ static SetlIteration termToIteration(final Term term) throws TermConversionException {
        if (term.size() != 3) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            try {
                final Expr        expr        = TermConverter.valueToExpr(term.firstMember());

                      SetlIterator    iterator    = null;
                if (! term.getMember(2).equals(new SetlString("nil"))) {
                    iterator  = SetlIterator.valueToIterator(term.getMember(2));
                }

                      Condition   cond        = null;
                if (! term.lastMember().equals(new SetlString("nil"))) {
                    cond    = TermConverter.valueToCondition(term.lastMember());
                }
                return new SetlIteration(expr, iterator, cond);
            } catch (final SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
            }
        }
    }
}

