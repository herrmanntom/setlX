package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.AssertException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.Condition;
import org.randoom.setlx.utilities.Environment;
import org.randoom.setlx.utilities.TermConverter;

/*
grammar rule:
statement
    : [...]
    | 'assert' '(' condition ',' anyExpr ')' ';'
    ;

implemented here as:
                   =========     =======
                   mCondition    mMessage
*/

public class Assert extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String     FUNCTIONAL_CHARACTER    = "^assert";

    private Condition mCondition;
    private Expr      mMessage;

    public Assert(Condition condition, Expr message) {
        mCondition  = condition;
        mMessage    = message;
    }

    protected void exec() throws SetlException {
        if ( ! mCondition.evalToBool()) {
            throw new AssertException("Assertion failed: " + mMessage.eval().toString());
        }
    }

    /* string operations */

    public String toString(int tabs) {
        return Environment.getLineStart(tabs) + "assert(" + mCondition.toString(tabs) + ", " + mMessage.toString(tabs) + ");";
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mCondition.toTerm());
        result.addMember(mMessage.toTerm());
        return result;
    }

    public static Assert termToStatement(Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Condition   condition   = TermConverter.valueToCondition(term.firstMember());
            Expr        message     = TermConverter.valueToExpr(term.lastMember());
            return new Assert(condition, message);
        }
    }
}

