package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.ExitException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * The exit statement -- the most important of them all!
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'exit' ';'
 *     ;
 */
public class Exit extends Statement {
    // functional character used in terms
    private final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Exit.class);

    /**
     * Singleton Exit statement.
     */
    public  final static Exit   E                    = new Exit();

    private Exit() { }

    @Override
    public ReturnMessage execute(final State state) throws ExitException {
        throw new ExitException();
    }

    @Override
    public void collectVariablesAndOptimize (
        final State        state,
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) { /* nothing to collect */ }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        sb.append("exit;");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 0);
        return result;
    }

    /**
     * Convert a term representing an Exit statement into such a statement.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting statement of this conversion.
     * @throws TermConversionException If term is malformed.
     */
    public static Exit termToStatement(final State state, final Term term) throws TermConversionException {
        if (term.size() != 0) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            return E;
        }
    }
}

