package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.MatchResult;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/**
 * The default branch in a match statement.
 *
 * grammar rule:
 * statement
 *     : [...]
 *     | 'match' '(' expr ')' '{' ( ... )* ('default' ':' block)? '}'
 *     ;
 *
 * implemented here as:
 *                                                        =====
 *                                                      statements
 */
public class MatchDefaultBranch extends MatchAbstractScanBranch {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(MatchDefaultBranch.class);
    /*package*/ final static int    END_OFFSET           = -2020202020;

    private final Block statements;

    public MatchDefaultBranch(final Block statements) {
        this.statements = statements;
    }

    @Override
    public MatchResult matches(final State state, final Value term) {
        return new MatchResult(true);
    }

    @Override
    public boolean evalConditionToBool(final State state) throws SetlException {
        return true;
    }

    @Override
    public MatchResult scannes(final State state, final SetlString string) {
        return new MatchResult(true);
    }

    @Override
    public int getEndOffset() {
        return END_OFFSET;
    }

    @Override
    public ReturnMessage execute(final State state) throws SetlException {
        return statements.execute(state);
    }

    @Override
    public void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        statements.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.appendLineStart(sb, tabs);
        sb.append("default:");
        sb.append(state.getEndl());
        statements.appendString(state, sb, tabs + 1);
        sb.append(state.getEndl());
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);
        result.addMember(state, statements.toTerm(state));
        return result;
    }

    public static MatchDefaultBranch termToBranch(final Term term) throws TermConversionException {
        if (term.size() != 1) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Block block = TermConverter.valueToBlock(term.firstMember());
            return new MatchDefaultBranch(block);
        }
    }
}

