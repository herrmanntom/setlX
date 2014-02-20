package org.randoom.setlx.expressionUtilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/**
 * Implementation of the most powerful expression in setlX: (set-)comprehension
 *
 * grammar rule:
 *
 *  * iterate
 *     : expr ':' iteratorChain ('|' condition)?
 *     ;
 *
 * implemented here as:
 *       ====     ========-----      =========
 *       expr       iterator         condition
 */
public class SetlIteration extends CollectionBuilder {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(SetlIteration.class);

    private final Expr         expr;
    private final SetlIterator iterator;
    private final Condition    condition;

    private class Exec implements SetlIteratorExecutionContainer {
        private final Expr            expr;
        private final Condition       condition;
        private final CollectionValue collection;

        public Exec (final Expr expr, final Condition condition, final CollectionValue collection) {
            this.expr       = expr;
            this.condition  = condition;
            this.collection = collection;
        }

        @Override
        public ReturnMessage execute(final State state, final Value lastIterationValue) throws SetlException {
            if (condition == null || condition.eval(state) == SetlBoolean.TRUE) {
                collection.addMember(state, expr.eval(state));
            }
            return null;
        }

        @Override
        public void collectVariablesAndOptimize (
            final List<String> boundVariables,
            final List<String> unboundVariables,
            final List<String> usedVariables
        ) {
            if (condition != null) {
                condition.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
            }
            expr.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
    }

    public SetlIteration(final Expr expr, final SetlIterator iterator, final Condition condition) {
        this.expr      = expr;
        this.iterator  = iterator;
        this.condition = condition;
    }

    @Override
    public void fillCollection(final State state, final CollectionValue collection) throws SetlException {
        iterator.eval(state, new Exec(expr, condition, collection));
    }

    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        iterator.collectVariablesAndOptimize(new Exec(expr, condition, null), boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb) {
        expr.appendString(state, sb, 0);
        sb.append(" : ");
        iterator.appendString(state, sb, 0);
        if (condition != null) {
            sb.append(" | ");
            condition.appendString(state, sb, 0);
        }
    }

    /* term operations */

    @Override
    public void addToTerm(final State state, final CollectionValue collection) {
        final Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(state, expr.toTerm(state));
        result.addMember(state, iterator.toTerm(state));
        if (condition != null) {
            result.addMember(state, condition.toTerm(state));
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
                final Expr expr = TermConverter.valueToExpr(term.firstMember());

                SetlIterator iterator = null;
                if (! term.getMember(2).equals(new SetlString("nil"))) {
                    iterator  = SetlIterator.valueToIterator(term.getMember(2));
                }

                Condition cond = null;
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

