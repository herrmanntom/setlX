package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressionUtilities.Condition;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'if' '(' condition ')' '{' block '}' ('else' 'if' '(' condition ')' '{' block '}')* ('else' '{' block '}')?
    ;

implemented here as:
                                                            =========         =====
                                                            mCondition     mStatements
*/

public class IfThenElseIfBranch extends IfThenAbstractBranch {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = "^ifThenElseIfBranch";

    private final Condition mCondition;
    private final Block     mStatements;

    public IfThenElseIfBranch(final Condition condition, final Block statements){
        mCondition  = condition;
        mStatements = statements;
    }

    @Override
    public boolean evalConditionToBool(final State state) throws SetlException {
        return mCondition.evalToBool(state);
    }

    @Override
    public ReturnMessage exec(final State state) throws SetlException {
        return mStatements.exec(state);
    }

    @Override
    protected ReturnMessage execute(final State state) throws SetlException {
        return exec(state);
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
    ) {
        mCondition.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        mStatements.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        sb.append(" else if (");
        mCondition.appendString(state, sb, tabs);
        sb.append(") ");
        mStatements.appendString(state, sb, tabs, true);
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, mCondition.toTerm(state));
        result.addMember(state, mStatements.toTerm(state));
        return result;
    }

    public static IfThenElseIfBranch termToBranch(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Condition condition   = TermConverter.valueToCondition(term.firstMember());
            final Block     block       = TermConverter.valueToBlock(term.lastMember());
            return new IfThenElseIfBranch(condition, block);
        }
    }
}

