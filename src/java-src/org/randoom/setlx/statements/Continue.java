package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * The continue statement.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'continue' ';'
 *     ;
 */
public class Continue extends Statement {
    // functional character used in terms
    private final static String   FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Continue.class);

    public  final static Continue C                    = new Continue();

    private Continue() {  }

    @Override
    public ReturnMessage execute(final State state) {
        return ReturnMessage.CONTINUE;
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
        sb.append("continue;");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        return new Term(FUNCTIONAL_CHARACTER, 0);
    }

    public static Continue termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 0) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            return C;
        }
    }
}

