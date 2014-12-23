package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.ValueStack;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

/**
 * Operator that gets a variable from the current scope and puts it on the stack.
 */
public class Variable extends AZeroOperator {

    private final String id;

    /**
     * Create a new Variable expression.
     *
     * @param id ID/name of this variable.
     */
    public Variable(final String id) {
        this.id = id;
    }

    @Override
    public Value evaluate(State state, ValueStack values) throws SetlException {
        return state.findValue(id);
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb) {
        sb.append(id);
    }

    @Override
    public Value modifyTerm(State state, Term term) {
        term.addMember(state, new SetlString(id));
        return term;
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Variable.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == Variable.class) {
            final Variable otr = (Variable) other;
            return id.compareTo(otr.id);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj.getClass() == Variable.class && id.equals(((Variable) obj).id);
    }

    @Override
    public int computeHashCode() {
        return ((int) COMPARE_TO_ORDER_CONSTANT) + id.hashCode();
    }
}
