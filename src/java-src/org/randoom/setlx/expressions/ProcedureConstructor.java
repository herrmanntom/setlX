package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.functions.PreDefinedProcedure;
import org.randoom.setlx.types.LambdaDefinition;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.Procedure;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * An expression creating a procedure.
 *
 * grammar rule:
 * definition
 *     : lambdaDefinition
 *     | procedureDefinition
 *     ;
 *
 * implemented here as:
 *       ===================
 *           mDefinition
 */
public class ProcedureConstructor extends Expr {
    // precedence level in SetlX-grammar
    private final static int          LAMBDA_PRECEDENCE    = 1050;
    private final static int          PRECEDENCE           = 9999;

    private final Procedure       definition;
    private       HashSet<String> closureVariables;

    public ProcedureConstructor(final Procedure definition) {
        this.definition       = definition;
        this.closureVariables = null;
    }

    @Override
    protected Procedure evaluate(final State state) throws SetlException {
        if (closureVariables == null) {
            this.optimize();
        }
        if (! closureVariables.isEmpty()) {
            final HashMap<String, Value> closure = new HashMap<>();
            for (final String var : closureVariables) {
                if (var.equals("this")) {
                    continue;
                }
                final Value val = state.findValue(var);
                if (val != Om.OM) {
                    if (val instanceof PreDefinedProcedure &&
                       var.equals(((PreDefinedProcedure)val).getName())
                    ) {
                        // skip predefined Functions bound to their name
                    } else {
                        closure.put(var, val);
                    }
                }
            }
            if (! closure.isEmpty()) {
                final Procedure result = definition.createCopy();
                result.setClosure(closure);
                return result;
            }
        }
        return definition;
    }

    @Override
    protected void collectVariables (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        final int preUnbound = unboundVariables.size();
        final int preUsed    = usedVariables.size();
        definition.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);

        final HashSet<String> closureVariables = new HashSet<>();
        closureVariables.addAll(unboundVariables.subList(preUnbound, unboundVariables.size()));
        closureVariables.addAll(usedVariables.subList(preUsed, usedVariables.size()));
        this.closureVariables = closureVariables;
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        definition.appendString(state, sb, tabs);
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        return definition.toTerm(state);
    }

    // precedence level in SetlX-grammar
    @Override
    public int precedence() {
        if (definition instanceof LambdaDefinition) {
            return LAMBDA_PRECEDENCE;
        } else {
            return PRECEDENCE;
        }
    }
}

