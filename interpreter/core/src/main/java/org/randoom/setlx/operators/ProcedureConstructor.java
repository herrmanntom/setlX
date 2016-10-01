package org.randoom.setlx.operators;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.functions.PreDefinedProcedure;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.types.*;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.SetlHashMap;
import org.randoom.setlx.utilities.State;

import java.util.ArrayDeque;
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
public class ProcedureConstructor extends AZeroOperator {
    // precedence level in SetlX-grammar
    private final static int      LAMBDA_PRECEDENCE = 1050;
    private final static int      PRECEDENCE        = 9999;

    private final Procedure       definition;
    private final boolean         isClosure;
    private       HashSet<String> closureVariables;

    /**
     * Create a new ProcedureConstructor expression.
     *
     * @param definition Base procedure definition.
     */
    public ProcedureConstructor(final Procedure definition) {
        this.definition       = definition;
        this.isClosure        = definition instanceof Closure;
        this.closureVariables = null;
    }

    @Override
    public boolean collectVariablesAndOptimize (
            final State        state,
            final List<String> boundVariables,
            final List<String> unboundVariables,
            final List<String> usedVariables
    ) {
        final int preUnbound = unboundVariables.size();
        final int preUsed    = usedVariables.size();
        definition.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);

        if (isClosure) {
            final HashSet<String> closureVariables = new HashSet<String>();

            // variables added to unbound where never assigned in this scope...
            // most likely they are predefined procedures
            closureVariables.addAll(unboundVariables.subList(preUnbound, unboundVariables.size()));

            // variables added to used are prebound ones detected as closure variables in definition.collectVariablesAndOptimize();
            closureVariables.addAll(usedVariables.subList(preUsed, usedVariables.size()));

            // remove some commonly encountered "unbound" variables
            closureVariables.remove("this");

            this.closureVariables = closureVariables;
        } else {
            // Prevent optimization. Too much overhead for just returning the definition...
            return false;
        }
        return true;
    }

    @Override
    public Procedure evaluate(State state, ArrayDeque<Value> values, OperatorExpression operatorExpression, int currentStackDepth) throws SetlException {
        if (isClosure) {
            if (closureVariables == null) {
                this.optimize(state);
            }
            final SetlHashMap<Value> closure = new SetlHashMap<Value>();
            if ( ! closureVariables.isEmpty()) {
                for (final String var : closureVariables) {
                    final Value val = state.findValue(var);
                    if (val != Om.OM) {
                        if (val instanceof PreDefinedProcedure &&
                                var.equals(((PreDefinedProcedure) val).getName())
                                ) {
                            // skip predefined Functions bound to their name
                            continue;
                        } else {
                            closure.put(var, val);
                        }
                    }
                }
            }
            // TODO add warning: throw new UndefinedOperationException("No valid closure variables detected - closure is empty!");
            final Closure result = ((Closure) definition).createCopy();
            result.setClosure(closure);
            return result;
        } else {
            return definition;
        }
    }

    /* string operations */

    @Override
    public void appendOperatorSign(final State state, final StringBuilder sb, List<String> expressions) {
        definition.appendString(state, sb, 0);
    }

    /* term operations */

    @Override
    public Value modifyTerm(final State state, Term term, ArrayDeque<Value> termFragments) throws SetlException {
        return definition.toTerm(state);
    }

    /* comparisons */

    @Override
    public final int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == ProcedureConstructor.class) {
            return definition.compareTo(((ProcedureConstructor) other).definition);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(ProcedureConstructor.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == ProcedureConstructor.class) {
            return definition.equals(((ProcedureConstructor) obj).definition);
        }
        return false;
    }

    @Override
    public final int computeHashCode() {
        return  ((int) compareToOrdering()) + definition.hashCode();
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

