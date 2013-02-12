package org.randoom.setlx.expressions;

import org.randoom.setlx.types.ClassDefinition;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/*
grammar rule:
objectConstructor
    : 'constructor' '(' procedureParameters ')' '{' block ('static' '{' block '}')? '}'
    ;

implemented here as:
      =================================================================================
                                          mDefinition
*/

public class ConstructorConstructor extends Expr {
    // precedence level in SetlX-grammar
    private final static int          PRECEDENCE           = 9999;

    protected final ClassDefinition mDefinition;

    public ConstructorConstructor(final ClassDefinition definition) {
        mDefinition = definition;
    }

    @Override
    protected Value evaluate(final State state) {
        return mDefinition.clone();
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
        mDefinition.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
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

