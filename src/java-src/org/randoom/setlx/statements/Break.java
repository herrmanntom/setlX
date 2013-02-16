package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;

import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'break' ';'
    ;
*/

public class Break extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER    = "^break";

    public  final static Break  B                       = new Break();

    private Break() { }

    @Override
    protected ReturnMessage execute(final State state) {
        return ReturnMessage.BREAK;
    }

    /* Gather all bound and unbound variables in this statement and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       Optimize sub-expressions during this process by calling optimizeAndCollectVariables()
       when adding variables from them.
    */
    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) { /* nothing to collect */ }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.getLineStart(sb, tabs);
        sb.append("break;");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        return new Term(FUNCTIONAL_CHARACTER, 0);
    }

    public static Break termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 0) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            return B;
        }
    }
}

