package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.IgnoreDummy;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.VariableScope;

import java.util.List;

/**
 * This class implements an ignored variable inside an assignable expression
 *
 * grammar rules:
 * assignable
 *     : variable   | idList      | '_'
 *     ;
 *
 * value
 *     : list | set | atomicValue | '_'
 *     ;
 *
 *                                  ===
 */
public class VariableIgnore extends AssignableExpression {
    // functional character used in terms
    public  final static String         FUNCTIONAL_CHARACTER = generateFunctionalCharacter(VariableIgnore.class);
    // precedence level in SetlX-grammar
    private final static int            PRECEDENCE           = 9999;

    public  final static VariableIgnore VI                   = new VariableIgnore();

    private VariableIgnore() { }

    @Override
    protected IgnoreDummy evaluate(final State state) throws UndefinedOperationException {
        return IgnoreDummy.ID;
    }

    @Override
    protected IgnoreDummy evaluateUnCloned(final State state) throws UndefinedOperationException {
        return IgnoreDummy.ID;
    }

    @Override
    protected void collectVariables (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) { /* nothing to collect */ }

    @Override
    public void collectVariablesWhenAssigned (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) { /* nothing to collect */ }

    // sets this expression to the given value
    @Override
    public void assignUncloned(final State state, final Value v, final String context) {
        // or maybe it just does nothing
    }

    @Override
    public boolean assignUnclonedCheckUpTo(final State state, final Value v, final VariableScope outerScope, final String context) {
        return true;
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append("_");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        return new Term(FUNCTIONAL_CHARACTER, 0);
    }

    public static VariableIgnore termToExpr(final Term term) {
        return VI;
    }

    // precedence level in SetlX-grammar
    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}

