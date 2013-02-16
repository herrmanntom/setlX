package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.IgnoreDummy;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.VariableScope;

import java.util.List;

/*
grammar rules:
assignable
    : variable   | idList      | '_'
    ;

value
    : list | set | atomicValue | '_'
    ;

this class implements an ignored variable inside an idList or expression:
                                 ===
*/

public class VariableIgnore extends AssignableExpression {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    public  final static String         FUNCTIONAL_CHARACTER = "^variableIgnore";
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

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    @Override
    protected void collectVariables (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) { /* nothing to collect */ }

    /* Gather all bound and unbound variables in this expression and its siblings
       when this expression gets assigned
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    @Override
    public void collectVariablesWhenAssigned (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) { /* nothing to collect */ }

    // sets this expression to the given value
    @Override
    public void assignUncloned(final State state, final Value v) {
        // or maybe it just does nothing
    }

    /* Similar to assignUncloned(),
       However, also checks if the variable is already defined in scopes up to
       (but EXCLUDING) `outerScope'.
       Returns true and sets `v' if variable is undefined or already equal to `v'.
       Returns false, if variable is defined and different from `v' */
    @Override
    public boolean assignUnclonedCheckUpTo(final State state, final Value v, final VariableScope outerScope) {
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

