package org.randoom.setlx.utilities;

import org.randoom.setlx.types.Value;

import java.util.ArrayList;
import java.util.List;

public abstract class CodeFragment {

    /* Gather all bound and unbound variables in this fragment and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used" */
    protected abstract void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    );

    public final void optimize() {
        final List<String> boundVariables   = new ArrayList<String>();
        final List<String> unboundVariables = new ArrayList<String>();
        final List<String> usedVariables    = new ArrayList<String>();
        collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    public abstract void appendString(final State state, final StringBuilder sb, final int tabs);

    public final String toString(final State state) {
        final StringBuilder sb     = new StringBuilder();
        appendString(state, sb, 0);
        return sb.toString();
    }

    @Override
    public final String toString() {
        final State         bubble = new StateImplementation();
        return toString(bubble);
    }

    /* term operations */

    public abstract Value toTerm(final State state);
}

