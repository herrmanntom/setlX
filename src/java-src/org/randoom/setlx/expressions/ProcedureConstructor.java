package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.functions.PreDefinedProcedure;
import org.randoom.setlx.types.LambdaProcedure;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.Procedure;
import org.randoom.setlx.utilities.SetlHashMap;
import org.randoom.setlx.utilities.State;

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
 *           definition
 */
public class ProcedureConstructor extends Expr {
    // precedence level in SetlX-grammar
    private final static int      LAMBDA_PRECEDENCE = 1050;
    private final static int      PRECEDENCE        = 9999;

    private final Procedure       definition;
    private       HashSet<String> closureVariables;

    /**
     * Create a new ProcedureConstructor expression.
     *
     * @param definition Base procedure definition.
     */
    public ProcedureConstructor(final Procedure definition) {
        this.definition       = definition;
        this.closureVariables = null;
    }

    @Override
    protected Procedure evaluate(final State state) throws SetlException {
        if (closureVariables == null) {
            this.optimize(state);
        }
        if (! closureVariables.isEmpty()) {
            final SetlHashMap<Value> closure = new SetlHashMap<Value>();
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
                        continue;
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
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        final int preUnbound = unboundVariables.size();
        final int preUsed    = usedVariables.size();
        definition.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);

        final HashSet<String> closureVariables = new HashSet<String>();
        closureVariables.addAll(unboundVariables.subList(preUnbound, unboundVariables.size()));
        closureVariables.addAll(usedVariables.subList(preUsed, usedVariables.size())); // TODO check why we need used here
        this.closureVariables = closureVariables;
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        definition.appendString(state, sb, tabs);
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) throws SetlException {
        return definition.toTerm(state);
    }

    @Override
    public int precedence() {
        if (definition instanceof LambdaProcedure) {
            return LAMBDA_PRECEDENCE;
        } else {
            return PRECEDENCE;
        }
    }
}

