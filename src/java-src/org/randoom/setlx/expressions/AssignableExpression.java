package org.randoom.setlx.expressions;

import java.util.List;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

public abstract class AssignableExpression extends Expr {

    /* Gather all bound and unbound variables in this expression and its siblings
       when this expression gets assigned
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
    */
    public abstract void collectVariablesWhenAssigned (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    );

    /*package*/ abstract Value evaluateUnCloned(final State state) throws SetlException;

    @Override
    public abstract void assignUncloned(final State state, final Value v) throws SetlException;

}