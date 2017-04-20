package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.assignments.AAssignableExpression;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * Implementation of the -= operator, on statement level.
 *
 * grammar rule:
 * assignmentOther
 *     : assignable ('-=' | [...] ) expr
 *     ;
 *
 * implemented here as:
 *       ==========                 ====
 *          lhs                     rhs
 */
public abstract class AbstractAssignment extends StatementWithPrintableResult {

    /** Expression to assign to.                */
    protected final AAssignableExpression lhs;
    /** Expression to evaluate.                 */
    protected final OperatorExpression           rhs;
    /** Enable to print result after execution. */
    protected       boolean                      printAfterEval;

    /**
     * Create new DirectAssignment.
     *
     * @param lhs Expression to assign to.
     * @param rhs Expression to evaluate.
     */
    protected AbstractAssignment(final AAssignableExpression lhs, final OperatorExpression rhs) {
        this.lhs            = lhs;
        this.rhs            = rhs;
        this.printAfterEval = false;
    }

    @Override
    /*package*/ final void setPrintAfterExecution() {
        printAfterEval = true;
    }

    @Override
    public final boolean collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        // first we evaluate lhs and rhs as usual
        lhs.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
        rhs.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);

        // then assign to lhs
        lhs.collectVariablesWhenAssigned(state, boundVariables, boundVariables, boundVariables);
        return false;
    }

    /* string operations */

    /**
     * Appends a string representation of this operator to the given
     * StringBuilder object.
     *
     * @see org.randoom.setlx.utilities.CodeFragment#toString(State)
     *
     * @param sb StringBuilder to append to.
     */
    public abstract void appendOperator(final StringBuilder sb);

    @Override
    public final void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        lhs.appendString(state, sb, tabs);
        appendOperator(sb);
        rhs.appendString(state, sb, tabs);
        sb.append(";");
    }

    /* term operations */

    /**
     * Get the functional character used in terms.
     *
     * @return functional character used in terms.
     */
    protected abstract String getFunctionalCharacter();

    @Override
    public final Term toTerm(final State state) throws SetlException {
        final Term result = new Term(getFunctionalCharacter(), 2);
        result.addMember(state, lhs.toTerm(state));
        result.addMember(state, rhs.toTerm(state));
        return result;
    }

    /* comparisons */

    @Override
    public final int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (this.getClass() == other.getClass()) {
            final AbstractAssignment otr = (AbstractAssignment) other;
            int cmp = lhs.compareTo(otr.lhs);
            if (cmp != 0) {
                return cmp;
            }
            return rhs.compareTo(otr.rhs);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (this.getClass() == obj.getClass()) {
            final AbstractAssignment other = (AbstractAssignment) obj;
            return lhs.equals(other.lhs) && rhs.equals(other.rhs);
        }
        return false;
    }

    @Override
    public final int computeHashCode() {
        int hash = ((int) compareToOrdering()) + lhs.hashCode();
        hash = hash * 31 + rhs.hashCode();
        return hash;
    }
}

