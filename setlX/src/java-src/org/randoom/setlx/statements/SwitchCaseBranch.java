package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Condition;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.TermConverter;

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

    public boolean evalConditionToBool() throws SetlException {
        return mCondition.evalToBool();
    }

    public Value execute() throws SetlException {
        return mStatements.execute();
    }

    protected Value exec() throws SetlException {
        return execute();
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

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(mCondition.toTerm());
        result.addMember(mStatements.toTerm());
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

