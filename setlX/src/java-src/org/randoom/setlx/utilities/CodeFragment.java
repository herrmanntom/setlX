package org.randoom.setlx.utilities;

import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.Value;

import java.util.ArrayList;
import java.util.List;

public abstract class CodeFragment {

    /* Gather all bound and unbound variables in this fragment and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used" */
    protected abstract void collectVariablesAndOptimize (
        final List<Variable>  boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    );

    public void optimize() {
        final List<Variable> boundVariables   = new ArrayList<Variable>();
        final List<Variable> unboundVariables = new ArrayList<Variable>();
        final List<Variable> usedVariables    = new ArrayList<Variable>();
        collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    public abstract void appendString(final StringBuilder sb, final int tabs);

    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        appendString(sb, 0);
        return sb.toString();
    }

    /* term operations */

    public abstract Value toTerm();
}

