package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.BreakException;
import org.randoom.setlx.exceptions.ContinueException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Term;
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

    private Condition mCondition;
    private Block     mStatements;

    public While(Condition condition, Block statements) {
        mCondition  = condition;
        mStatements = statements;
    }

    public void exec() throws SetlException {
        boolean finishLoop  = sFinishLoop;
        if (finishLoop) { // unset, because otherwise it would be reset when this loop finishes
            Environment.setDebugFinishLoop(false);
        }
        while (mCondition.evalToBool()) {
            try{
                mStatements.execute();
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
    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getLineStart(tabs);
        result += "while (" + mCondition.toString(tabs) + ") ";
        result += mStatements.toString(tabs, true);
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mCondition.toTerm());
        result.addMember(mStatements.toTerm());
        return result;
    }

    public static While termToStatement(Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Condition   condition   = TermConverter.valueToCondition(term.firstMember());
            Block       block       = TermConverter.valueToBlock(term.lastMember());
            return new While(condition, block);
        }
    }
}

