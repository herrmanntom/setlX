package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

public abstract class AssignableExpression extends Expr {

    /*package*/ abstract Value evaluateUnCloned(final State state) throws SetlException;

    @Override
    public abstract void assignUncloned(final State state, final Value v) throws SetlException;

}