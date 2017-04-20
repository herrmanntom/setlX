package org.randoom.setlx.operatorUtilities;

import org.randoom.setlx.assignments.AAssignableExpression;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.StopExecutionException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.ImmutableCodeFragment;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermUtilities;
import org.randoom.setlx.utilities.VariableScope;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a chainable SetlX iterator element.
 *
 * grammar rules:
 * iteratorChain
 *     : iterator                       (',' iterator)*
 *     ;
 *
 * iterator
 *     : assignable 'in' expr
 *     ;
 *
 * implemented here as:
 *       ==========      ====        ||      ========
 *       assignable   collection     ||        next
 */
public class SetlIterator extends ImmutableCodeFragment {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(SetlIterator.class);

    private final AAssignableExpression assignable;
    private final OperatorExpression           collection;
    private       SetlIterator                 next;       // next iterator in iteratorChain

    /**
     * Create a new SetlIterator.
     *
     * @param assignable Expression assigned during the iteration.
     * @param collection Collection to iterate over.
     */
    public SetlIterator(final AAssignableExpression assignable, final OperatorExpression collection) {
        this(assignable, collection, null);
    }

    private SetlIterator(final AAssignableExpression assignable, final OperatorExpression collection, final SetlIterator next) {
        this.assignable = assignable;
        this.collection = collection;
        this.next       = next;
    }

    /**
     * Add next iterator to end of current iterator chain.
     *
     * @param nextIterator Iterator element to add.
     */
    public void add(final SetlIterator nextIterator) {
        if (next == null) {
            next = nextIterator;
        } else {
            next.add(nextIterator);
        }
    }

    /**
     * Evaluate this iteration, by executing provided container in scope created
     *  by this iteration.
     *
     * Note: Resets to outer scope after iteration is finished!
     * Note: Each iterator introduces a new scope to allow its iteration
     *       variable to be local.
     * Note: Variables inside the whole iteration are not _not_ local
     *       and will be written `through' these inner scopes.
     *
     * @param state          Current state of the running setlX program.
     * @param exec           Code fragments inside this specific iteration.
     * @return               Result of the evaluation.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public ReturnMessage eval(final State state, final SetlIteratorExecutionContainer exec) throws SetlException {
        final VariableScope outerScope = state.getScope();
        try {
            final ReturnMessage result = evaluate(state, exec, outerScope);

            if (result == ReturnMessage.BREAK) {
                return null; // remove break message
            }

            return result;
        } finally { // make sure scope is always reset
            state.setScope(outerScope);
        }
    }

    /**
     * Gather all bound and unbound variables in this fragment and its siblings.
     * Optimizes this fragment, if this can be safely done.
     *
     * @param state            Current state of the running setlX program.
     * @param container        Code fragments inside this specific iteration.
     * @param boundVariables   Variables "assigned" in this fragment.
     * @param unboundVariables Variables not present in bound when used.
     * @param usedVariables    Variables present in bound when used.
     * @return true iff this fragment may be optimized if it is constant.
     */
    public boolean collectVariablesAndOptimize (
        final State                          state,
        final SetlIteratorExecutionContainer container,
        final List<String>                   boundVariables,
        final List<String>                   unboundVariables,
        final List<String>                   usedVariables
    ) {
        final List<String> tempVariables = new ArrayList<>();
        return collectVariablesAndOptimize(state, container, tempVariables, boundVariables, unboundVariables, usedVariables);
    }

    /**
     * Gather all bound and unbound variables in this fragment and its siblings.
     * Optimizes this fragment, if this can be safely done.
     *
     * @param state            Current state of the running setlX program.
     * @param container        Code fragments inside this specific iteration.
     * @param tempVariables    Variables temporarily assigned in this iterator.
     * @param boundVariables   Variables "assigned" in this fragment.
     * @param unboundVariables Variables not present in bound when used.
     * @param usedVariables    Variables present in bound when used.
     * @return true iff this fragment may be optimized if it is constant.
     */
    public boolean collectVariablesAndOptimize (
        final State                          state,
        final org.randoom.setlx.operatorUtilities.SetlIteratorExecutionContainer container,
        final List<String>                   tempVariables,
        final List<String>                   boundVariables,
        final List<String>                   unboundVariables,
        final List<String>                   usedVariables
    ) {
        boolean allowOptimization = collection.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);

        /* Variables in this expression get assigned temporarily.
           Collect them into a temporary list, add them to boundVariables and
           remove them again before returning. */
        final List<String> tempVars = new ArrayList<>();
        allowOptimization = assignable.collectVariablesWhenAssigned(state, tempVars, unboundVariables, usedVariables)
                && allowOptimization;

        final int preIndex = boundVariables.size();
        boundVariables.addAll(tempVars);
        tempVariables.addAll(tempVars);

        if (next != null) {
            allowOptimization = next.collectVariablesAndOptimize(state, container, tempVariables, boundVariables, unboundVariables, usedVariables)
                    && allowOptimization;
        } else {
            allowOptimization = container.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables)
                    && allowOptimization;
        }

        // remove the added variables (DO NOT use removeAll(); same variable name could be there multiple times!)
        for (int i = tempVars.size(); i > 0; --i) {
            boundVariables.remove(preIndex + (i - 1));
        }
        return allowOptimization;
    }

    @Override
    public boolean collectVariablesAndOptimize (
        final State                    state,
        final List<String>             boundVariables,
        final List<String>             unboundVariables,
        final List<String>             usedVariables
    ) {
        throw new UnsupportedOperationException("Iterators can only be optimized together with their execution container.");
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        assignable.appendString(state, sb, 0);
        sb.append(" in ");
        collection.appendString(state, sb, 0);
        if (next != null) {
            sb.append(", ");
            next.appendString(state, sb, tabs);
        }
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 3);
        result.addMember(state, assignable.toTerm(state));
        result.addMember(state, collection.toTerm(state));
        if (next != null) {
            result.addMember(state, next.toTerm(state));
        } else {
            result.addMember(state, SetlString.NIL);
        }
        return result;
    }

    /**
     * Convert a term representing a SetlIterator into such a CodeFragment.
     *
     * @param state                    Current state of the running setlX program.
     * @param value                    Term to convert.
     * @return                         Resulting SetlIterator.
     * @throws org.randoom.setlx.exceptions.TermConversionException Thrown in case of an malformed term.
     */
    public static SetlIterator valueToIterator(final State state, final Value value) throws TermConversionException {
        if (value.getClass() != Term.class) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            try {
                final Term   term = (Term) value;
                final String fc   = term.getFunctionalCharacter();
                if (! fc.equals(FUNCTIONAL_CHARACTER) || term.size() != 3) {
                    throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
                }

                final AAssignableExpression assignable = TermUtilities.valueToAssignableExpr(state, term.firstMember());
                final OperatorExpression collection = OperatorExpression.createFromTerm(state, term.getMember(2));

                SetlIterator iterator = null;
                if (! term.lastMember().equals(SetlString.NIL)) {
                    iterator = SetlIterator.valueToIterator(state, term.lastMember());
                }
                return new SetlIterator(assignable, collection, iterator);
            } catch (final SetlException se) {
                throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER, se);
            }
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        }if (this.getClass() == other.getClass()) {
            SetlIterator otr = (SetlIterator) other;
            int cmp = assignable.compareTo(otr.assignable);
            if (cmp != 0) {
                return cmp;
            }
            cmp = collection.compareTo(otr.collection);
            if (cmp != 0) {
                return cmp;
            }
            if (next != null) {
                if (otr.next != null) {
                    return next.compareTo(otr.next);
                } else {
                    return -1;
                }
            } if (otr.next != null) {
                return 1;
            }
            return 0;
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(SetlIterator.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == SetlIterator.class) {
            SetlIterator setlIterator = (SetlIterator) obj;
            if (assignable.equals(setlIterator.assignable) && collection.equals(setlIterator.collection)) {
                if (next != null && setlIterator.next != null) {
                    return next.equals(setlIterator.next);
                } else if (next == null && setlIterator.next == null) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + assignable.hashCode();
        hash = hash * 31 + collection.hashCode();
        if (next != null) {
            hash = hash * 31 + next.hashCode();
        }
        return hash;
    }

    /* private functions */

    private ReturnMessage evaluate(final State state, final SetlIteratorExecutionContainer exec, final VariableScope outerScope) throws SetlException {
        final Value iterationValue = collection.evaluate(state); // trying to iterate over this value
        if (iterationValue instanceof CollectionValue) {
            final CollectionValue coll       = (CollectionValue) iterationValue.clone();
            // scope for inner execution/next iterator
            final VariableScope   innerScope = state.getScope().createIteratorBlock();
            // iterate over items
            for (final Value v: coll) {
                if (state.executionStopped) {
                    throw new StopExecutionException();
                }

                // restore inner scope
                state.setScope(innerScope);
                // force iteration variables to be local to this block
                final int writeThroughToken = innerScope.unsetWriteThrough();

                // assign value from collection
                final boolean successful = assignable.assignUnclonedCheckUpTo(state, v.clone(), outerScope, false, FUNCTIONAL_CHARACTER);

                // reset WriteThrough, because changes during execution are not strictly local
                innerScope.setWriteThrough(writeThroughToken);

                /*
                 * This check is done to allow the following statement to work as expected:
                 *
                 * for (x in s, [x,y] in t) {...}
                 *
                 * where the the loop is only executed, when the first
                 * `x' is equal to the second `x'
                 */
                if ( ! successful) {
                    continue;
                }

                /* Starts iteration of next iterator or execution if this is the
                   last iterator.
                   Stops iteration if requested by execution.                 */
                ReturnMessage result;
                if (next != null) {
                    result = next.evaluate(state, exec, outerScope);
                } else {
                    result = exec.execute(state, v);
                }
                if (result != null) {
                    if (result == ReturnMessage.CONTINUE) {
                        continue;
                    } /* else if (result == ReturnMessage.BREAK) {
                        return result; // also break next iterator
                    } */
                    return result;
                }
            }
            return null;
        } else {
            throw new IncompatibleTypeException(
                "Evaluation of iterator '" + iterationValue + "' is not a collection value."
            );
        }
    }
}

