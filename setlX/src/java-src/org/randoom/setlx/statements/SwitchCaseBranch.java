package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Condition;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'switch' '{' ('case' condition ':' block)* ('default' ':' block)? '}'
    ;

implemented here as:
                           =========     =====
                           mCondition mStatements
*/

public class SwitchCaseBranch extends SwitchAbstractBranch {
    // functional character used in terms
    /*package*/ final static String FUNCTIONAL_CHARACTER = "^switchCaseBranch";

    private final Condition mCondition;
    private final Block     mStatements;

    public SwitchCaseBranch(final Condition condition, final Block statements){
        mCondition  = condition;
        mStatements = statements;
    }

    public boolean evalConditionToBool(final State state) throws SetlException {
        return mCondition.evalToBool(state);
    }

    public Value execute(final State state) throws SetlException {
        return mStatements.execute(state);
    }

    protected Value exec(final State state) throws SetlException {
        return execute(state);
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
    ) {
        mCondition.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        mStatements.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        Environment.getLineStart(sb, tabs);
        sb.append("case ");
        mCondition.appendString(sb, tabs);
        sb.append(":");
        sb.append(Environment.getEndl());
        mStatements.appendString(sb, tabs + 1, false);
        sb.append(Environment.getEndl());
    }

    /* term operations */

    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(mCondition.toTerm(state));
        result.addMember(mStatements.toTerm(state));
        return result;
    }

    public static SwitchCaseBranch termToBranch(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Condition condition   = TermConverter.valueToCondition(term.firstMember());
            final Block     block       = TermConverter.valueToBlock(term.lastMember());
            return new SwitchCaseBranch(condition, block);
        }
    }
}

