package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;

/**
 * Stack of operators that can be evaluated.
 */
public abstract class Expression extends ImmutableCodeFragment {

    /**
     * Evaluate this expression.
     *
     * @param state          Current state of the running setlX program.
     * @return               Result of the evaluation.
     * @throws org.randoom.setlx.exceptions.SetlException Thrown in case of some (user-) error.
     */
    public abstract Value evaluate(final State state) throws SetlException;
}
