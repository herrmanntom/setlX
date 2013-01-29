package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.functions.PreDefinedFunction;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.ProcedureDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private       Set<Variable>       mClosureVariables;

    public ProcedureConstructor(final ProcedureDefinition definition) {
        mDefinition       = definition;
        mClosureVariables = null;
    }

    @Override
    protected ProcedureDefinition evaluate(final State state) throws SetlException {
        if (mClosureVariables == null) {
            this.optimize();
        }
        if (! mClosureVariables.isEmpty()) {
            final HashMap<Variable, Value> closure = new HashMap<Variable, Value>();
            for (final Variable var : mClosureVariables) {
                final Value val = var.eval(state);
                if (val != Om.OM) {
                    if (val instanceof PreDefinedFunction &&
                       var.toString().equals(((PreDefinedFunction)val).getName())
                    ) {
                        // skip predefined Functions bound to their name
                    } else {
                        closure.put(var, val);
                    }
                }
            }
            if (! closure.isEmpty()) {
                final ProcedureDefinition result = mDefinition.createCopy();
                result.addClosure(closure);
                return result;
            }
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
    @Override
    protected void collectVariables (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        final int preUnbound = unboundVariables.size();
        final int preUsed    = usedVariables.size();
        mDefinition.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);

        final HashSet<Variable> closureVariables = new HashSet<Variable>();
        closureVariables.addAll(unboundVariables.subList(preUnbound, unboundVariables.size()));
        closureVariables.addAll(usedVariables.subList(preUsed, usedVariables.size()));
        mClosureVariables = closureVariables;
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        mDefinition.appendString(state, sb, tabs);
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        return mDefinition.toTerm(state);
    }

    // precedence level in SetlX-grammar
    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}

