package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.BreakException;
import org.randoom.setlx.exceptions.ContinueException;
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
            try{
                result = mStatements.execute();
                if (result != null) {
                    return result;
                }
            } catch (ContinueException e) {
                continue;
            } catch (BreakException e) {
                break;
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

    /* string operations */

    public String toString(final int tabs) {
        String result = Environment.getLineStart(tabs);
        result += "while (" + mCondition.toString(tabs) + ") ";
        result += mStatements.toString(tabs, true);
        return result;
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

