package org.randoom.setlx.operatorUtilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;

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
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(SetlIteration.class);

    private final OperatorExpression expr;
    private final SetlIterator iterator;
    private final Condition condition;

    private static final class Exec implements SetlIteratorExecutionContainer {
        private final OperatorExpression expr;
        private final Condition condition;
        private final CollectionValue collection;

        public Exec (final OperatorExpression expr, final Condition condition, final CollectionValue collection) {
            this.expr       = expr;
            this.condition  = condition;
            this.collection = collection;
        }

        @Override
        public ReturnMessage execute(final State state, final Value lastIterationValue) throws SetlException {
            if (condition == null || condition.evaluate(state) == SetlBoolean.TRUE) {
                collection.addMember(state, expr.evaluate(state));
            }
            return null;
        }

        @Override
        public boolean collectVariablesAndOptimize (
            final State        state,
            final List<String> boundVariables,
            final List<String> unboundVariables,
            final List<String> usedVariables
        ) {
            boolean allowOptimization = true;
            if (condition != null) {
                allowOptimization = condition.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
            }
            return expr.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables)
                    && allowOptimization;

        }
    }

    /**
     * Create a new SetlIteration.
     *
     * @param expr      Expression to evaluate before adding to collection.
     * @param iterator  Iteration definition.
     * @param condition Loop condition.
     */
    public SetlIteration(final OperatorExpression expr, final SetlIterator iterator, final Condition condition) {
        this.expr      = expr;
        this.iterator  = iterator;
        this.condition = condition;
    }

    @Override
    public void fillCollection(final State state, final CollectionValue collection) throws SetlException {
        iterator.eval(state, new Exec(expr, condition, collection));
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        return iterator.collectVariablesAndOptimize(state, new Exec(expr, condition, null), boundVariables, unboundVariables, usedVariables);
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
    public void addToTerm(final State state, final CollectionValue collection) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(state, expr.toTerm(state));
        result.addMember(state, iterator.toTerm(state));
        if (condition != null) {
            result.addMember(state, condition.toTerm(state));
        } else {
            result.addMember(state, SetlString.NIL);
        }
        collection.addMember(state, result);
    }

    /**
     * Regenerate SetlIteration from a term representing this expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term representation.
     * @return                         Regenerated SetlIteration.
     * @throws TermConversionException Thrown in case the term is malformed.
     */
    /*package*/ static SetlIteration termToIteration(final State state, final Term term) throws TermConversionException {
        if (term.size() != 3) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            try {
                final OperatorExpression expr = OperatorExpression.createFromTerm(state, term.firstMember());

                final SetlIterator iterator = SetlIterator.valueToIterator(state, term.getMember(2));

                Condition cond = null;
                if (! term.lastMember().equals(SetlString.NIL)) {
                    cond = TermUtilities.valueToCondition(state, term.lastMember());
                }
                return new SetlIteration(expr, iterator, cond);
            } catch (final SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER, se);
            }
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
        } else if (other.getClass() == SetlIteration.class) {
            SetlIteration setlIteration = (SetlIteration) other;
            int cmp = expr.compareTo(setlIteration.expr);
            if (cmp != 0) {
                return cmp;
            }
            cmp = iterator.compareTo(setlIteration.iterator);
            if (cmp != 0) {
                return cmp;
            }
            if (condition != null) {
                if (setlIteration.condition != null) {
                    return condition.compareTo(setlIteration.condition);
                } else {
                    return -1;
                }
            } if (setlIteration.condition != null) {
                return 1;
            }
            return 0;
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(SetlIteration.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(final Object obj, boolean ordered) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == SetlIteration.class) {
            SetlIteration setlIteration = (SetlIteration) obj;
            if (expr.equals(setlIteration.expr) && iterator.equals(setlIteration.iterator)) {
                if (condition != null && setlIteration.condition != null) {
                    return condition.equals(setlIteration.condition);
                } else if (condition == null && setlIteration.condition == null) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public int computeHashCode(boolean ordered) {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + expr.hashCode();
        hash = hash * 31 + iterator.hashCode();
        if (condition != null) {
            hash = hash * 31 + condition.hashCode();
        }
        return hash;
    }
}

