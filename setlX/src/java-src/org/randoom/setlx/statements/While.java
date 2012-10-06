package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Condition;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'while' '(' condition ')' '{' block '}'
    ;

implemented here as:
                  =========         =====
                  mCondition     mStatements
*/

public class While extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String  FUNCTIONAL_CHARACTER   = "^while";
    // continue execution of this loop in debug mode until it finishes. MAY ONLY BE SET BY ENVIRONMENT CLASS!
    public        static boolean sFinishLoop            = false;

    private final Condition mCondition;
    private final Block     mStatements;

    public While(final Condition condition, final Block statements) {
        mCondition  = condition;
        mStatements = statements;
    }

    protected Value exec() throws SetlException {
        final boolean finishLoop  = sFinishLoop;
        if (finishLoop) { // unset, because otherwise it would be reset when this loop finishes
            Environment.setDebugFinishLoop(false);
        }
        Value result = null;
        while (mCondition.evalToBool()) {
            result = mStatements.execute();
            if (result != null) {
                if (result == Om.OM) {
                    if (Om.OM.isContinue()) {
                        continue;
                    } else if (Om.OM.isBreak()) {
                        break;
                    }
                }
                return result;
            }
        }
        if (sFinishLoop) {
            Environment.setDebugModeActive(true);
            Environment.setDebugFinishLoop(false);
        } else if (finishLoop) {
            Environment.setDebugFinishLoop(true);
        }
        return null;
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
        sb.append("while (");
        mCondition.appendString(sb, tabs);
        sb.append(") ");
        mStatements.appendString(sb, tabs, true);
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(mCondition.toTerm());
        result.addMember(mStatements.toTerm());
        return result;
    }

    public static While termToStatement(Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Condition condition   = TermConverter.valueToCondition(term.firstMember());
            final Block     block       = TermConverter.valueToBlock(term.lastMember());
            return new While(condition, block);
        }
    }
}

