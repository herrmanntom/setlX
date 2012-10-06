package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.ProcedureDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/*
grammar rule:
definition
    : lambdaDefinition
    | procedureDefinition
    ;

implemented here as:
      ===================
          mDefinition
*/

public class ProcedureConstructor extends Expr {
    // precedence level in SetlX-grammar
    private final static int          PRECEDENCE           = 9999;

    private final ProcedureDefinition mDefinition;
    private       List<Variable>      mClosureVariables;

    public ProcedureConstructor(final ProcedureDefinition definition) {
        mDefinition       = definition;
        mClosureVariables = null;
    }

    protected ProcedureDefinition evaluate() throws SetlException {
        if (mClosureVariables == null) {
            this.optimize();
        }
        if (mClosureVariables.size() > 0) {
            final HashMap<Variable, Value> closure = new HashMap<Variable, Value>();
            for (final Variable var : mClosureVariables) {
                final Value val = var.eval();
                if (val != Om.OM) {
                    closure.put(var, val);
                }
            }
            final ProcedureDefinition result = mDefinition.createCopy();
            result.addClosure(closure);
            return result;
        }
        return mDefinition;
    }

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    protected void collectVariables (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        final int preUnbound = unboundVariables.size();
        final int preUsed    = usedVariables.size();
        mDefinition.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);

        mClosureVariables = new ArrayList<Variable>();
        mClosureVariables.addAll(unboundVariables.subList(preUnbound, unboundVariables.size()));
        mClosureVariables.addAll(usedVariables.subList(preUsed, usedVariables.size()));
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        mDefinition.appendString(sb, tabs);
    }

    /* term operations */

    public Value toTerm() {
        return mDefinition.toTerm();
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}

