package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.operatorUtilities.ValueStack;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

/**
 * Base class for operators that take no arguments.
 * They are in fact not really operators.
 */
public abstract class AZeroOperator extends AOperator {
    @Override
    public boolean hasArgumentBeforeOperator() {
        return false;
    }

    @Override
    public boolean hasArgumentAfterOperator() {
        return false;
    }

    @Override
    public boolean isLeftAssociative() {
        return false;
    }

    @Override
    public boolean isRightAssociative() {
        return false;
    }

    @Override
    public int precedence() {
        return 9999;
    }

    @Override
    public Value buildTerm(State state, ValueStack termFragments) throws SetlException {
        Term term = new Term(generateFunctionalCharacter(this.getClass()));
        return modifyTerm(state, term);
    }

    /**
     * Modify default term for this operator.
     *
     * @param state          Current state of the running setlX program.
     * @param term           Term to work with.
     * @return               Resulting term.
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value modifyTerm(State state, Term term) throws SetlException {
        return term;
    }
}
