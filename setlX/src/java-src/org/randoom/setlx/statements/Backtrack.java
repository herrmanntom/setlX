package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.BacktrackException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.Environment;

import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'backtrack' ';'
    ;
*/

public class Backtrack extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String    FUNCTIONAL_CHARACTER = "^backtrack";

    public  final static Backtrack BT                   = new Backtrack();

    private Backtrack() { }

    protected Value exec() throws BacktrackException {
        throw new BacktrackException("Backtrack-statement was executed outside of check-statement.");
    }

    /* Gather all bound and unbound variables in this statement and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       Optimize sub-expressions during this process by calling optimizeAndCollectVariables()
       when adding variables from them.
    */
    public void collectVariablesAndOptimize (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) { /* nothing to collect */ }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        Environment.getLineStart(sb, tabs);
        sb.append("backtrack;");
    }

    /* term operations */

    public Term toTerm() {
        return new Term(FUNCTIONAL_CHARACTER, 0);
    }

    public static Backtrack termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 0) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            return BT;
        }
    }
}

