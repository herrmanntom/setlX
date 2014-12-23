package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.ValueStack;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

/**
 * Base class for binary infix operators.
 */
public abstract class ABinaryInfixOperator extends AOperator {
    @Override
    public boolean hasArgumentBeforeOperator() {
        return true;
    }

    @Override
    public boolean hasArgumentAfterOperator() {
        return true;
    }

    @Override
    public Value buildTerm(State state, ValueStack termFragments) throws SetlException {
        Value rhs = termFragments.poll();
        Value lhs = termFragments.poll();
        Term term = new Term(generateFunctionalCharacter(this.getClass()));
        term.addMember(state, lhs);
        term.addMember(state, rhs);
        return term;
    }

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other || this.getClass() == other.getClass()) {
            return 0;
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || this.getClass() == obj.getClass();
    }
}
