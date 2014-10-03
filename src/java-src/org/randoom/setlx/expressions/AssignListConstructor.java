package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressionUtilities.ExplicitList;
import org.randoom.setlx.types.IndexedCollectionValue;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.VariableScope;

import java.util.List;

/**
 * Implementation of expression creating an assignable list.
 *
 * grammar rules:
 * assignable
 *     : '[' explicitAssignList ']'
 *     | [..]
 *     ;
 *
 * explicitAssignList
 *     : assignable (',' assignable)*
 *     ;
 *
 * implemented here as:
 *           ==================
 *                  list
 */
public class AssignListConstructor extends AssignableExpression {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(AssignListConstructor.class);
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 9999;

    private final ExplicitList list;

    /**
     * Constructor.
     *
     * @param constructor Constructor for explicit list of variables.
     */
    public AssignListConstructor(final ExplicitList constructor) {
        this.list = constructor;
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        final SetlList list = new SetlList();
        this.list.fillCollection(state, list);
        list.compress();
        return list;
    }

    @Override
    Value evaluateUnCloned(final State state) throws SetlException {
        return evaluate(state);
    }

    @Override
    protected void collectVariables (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        list.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
    }

    @Override
    public void collectVariablesWhenAssigned (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        list.collectVariablesWhenAssigned(state, boundVariables, unboundVariables, usedVariables);
    }

    @Override
    public void assignUncloned(final State state, final Value value, final String context) throws SetlException {
        if (value instanceof IndexedCollectionValue) {
            list.assignUncloned(state, (IndexedCollectionValue) value, context);
        } else {
            throw new IncompatibleTypeException(
                "The value '" + value.toString(state) + "' is unusable for assignment to \"" + this.toString(state) + "\"."
            );
        }
    }

    @Override
    public boolean assignUnclonedCheckUpTo(final State state, final Value value, final VariableScope outerScope, final boolean checkObjects, final String context) throws SetlException {
        if (value instanceof IndexedCollectionValue) {
            return list.assignUnclonedCheckUpTo(state, (IndexedCollectionValue) value, outerScope, checkObjects, context);
        } else {
            throw new IncompatibleTypeException(
                "The value '" + value.toString(state) + "' is unusable for assignment to \"" + this.toString(state) + "\"."
            );
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append("[");
        list.appendString(state, sb);
        sb.append("]");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);

        final SetlList list = new SetlList();
        this.list.addToTerm(state, list);

        result.addMember(state, list);

        return result;
    }

    /**
     * Convert a term representing a AssignListConstructor into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting expression.
     * @throws TermConversionException Thrown in case of a malformed term.
     */
    public static AssignListConstructor termToExpr(final State state, final Term term) throws TermConversionException {
        if (term.size() != 1 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList     list = (SetlList) term.firstMember();
            final ExplicitList el   = ExplicitList.collectionValueToExplicitList(state, list);
            return new AssignListConstructor(el);
        }
    }

    /* comparisons */

    @Override
    public int compareTo(final Expr other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == AssignListConstructor.class) {
            final AssignListConstructor assignListConstructor = (AssignListConstructor) other;
            return list.compareTo(assignListConstructor.list);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(AssignListConstructor.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == AssignListConstructor.class) {
            final AssignListConstructor assignListConstructor = (AssignListConstructor) obj;
            return list.equals(assignListConstructor.list);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + list.hashCode();
    }

    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}

