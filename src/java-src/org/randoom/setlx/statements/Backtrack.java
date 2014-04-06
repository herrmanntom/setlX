package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.BacktrackException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * The backtrack statement, which is used when implementing a backtrack-like
 * algorithm with the check-statement.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'backtrack' ';'
 *     ;
 */
public class Backtrack extends Statement {
    // functional character used in terms
    private final static String    FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Backtrack.class);

    /**
     * Singleton Backtrack statement.
     */
    public  final static Backtrack BT                   = new Backtrack();

    private Backtrack() { }

    @Override
    public ReturnMessage execute(final State state) throws BacktrackException {
        throw new BacktrackException();
    }

    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) { /* nothing to collect */ }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        sb.append("backtrack;");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        return new Term(FUNCTIONAL_CHARACTER, 0);
    }

    /**
     * Convert a term representing a Backtrack statement into such a statement.
     *
     * @param term                     Term to convert.
     * @return                         Resulting statement of this conversion.
     * @throws TermConversionException If term is malformed.
     */
    public static Backtrack termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 0) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            return BT;
        }
    }
}

