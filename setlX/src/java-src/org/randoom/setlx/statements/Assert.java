package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.AssertException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
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
    | 'assert' '(' condition ',' anyExpr ')' ';'
    ;

implemented here as:
                   =========     =======
                   mCondition    mMessage
*/

public class Assert extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String     FUNCTIONAL_CHARACTER    = "^assert";

    private final Condition mCondition;
    private final Expr      mMessage;

    public Assert(final Condition condition, final Expr message) {
        mCondition  = condition;
        mMessage    = message;
    }

    protected Value exec() throws SetlException {
        if ( ! mCondition.evalToBool()) {
            throw new AssertException("Assertion failed: " + mMessage.eval().toString());
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
        mMessage.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        Environment.getLineStart(sb, tabs);
        sb.append("assert(");
        mCondition.appendString(sb, tabs);
        sb.append(", ");
        mMessage.appendString(sb, tabs);
        sb.append(");");
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(mCondition.toTerm());
        result.addMember(mMessage.toTerm());
        return result;
    }

    public static Assert termToStatement(Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Condition condition   = TermConverter.valueToCondition(term.firstMember());
            final Expr      message     = TermConverter.valueToExpr(term.lastMember());
            return new Assert(condition, message);
        }
    }
}

