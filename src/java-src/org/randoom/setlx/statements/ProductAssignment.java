package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/*
grammar rule:
assignmentOther
    : assignable ('*=' | [...] ) anyExpr
    ;

implemented here as:
      ==========                 =======
         mLhs                     mRhs
*/

public class ProductAssignment extends StatementWithPrintableResult {
    // functional character used in terms
    public  final static String     FUNCTIONAL_CHARACTER    = "^productAssignment";

    // precedence level in SetlX-grammar
    private final static int        PRECEDENCE              = 1000;

    private final Expr    mLhs;
    private final Expr    mRhs;
    private       boolean mPrintAfterEval;

    public ProductAssignment(final Expr lhs, final Expr rhs) {
        mLhs            = lhs;
        mRhs            = rhs;
        mPrintAfterEval = false;
    }

    /*package*/ @Override
    void setPrintAfterEval() {
        mPrintAfterEval = true;
    }

    @Override
    protected ReturnMessage execute(final State state) throws SetlException {
        final Value assigned = mLhs.eval(state).productAssign(state, mRhs.eval(state).clone());
        mLhs.assignUncloned(state, assigned);

        if (state.traceAssignments) {
            state.outWriteLn("~< Trace: " + mLhs + " := " + assigned + " >~");
        } else if (mPrintAfterEval) {
            printResult(state, assigned);
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
    @Override
    public void collectVariablesAndOptimize (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        // first we evaluate lhs and rhs as usual
        mLhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        mRhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);

        // then assign to mLhs
        // add all variables found to bound by not supplying unboundVariables
        // as this expression is now used in an assignment
        mLhs.collectVariablesAndOptimize(boundVariables, boundVariables, boundVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.getLineStart(sb, tabs);
        mLhs.appendString(state, sb, tabs);
        sb.append(" *= ");
        mRhs.appendString(state, sb, tabs);
        sb.append(";");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, mLhs.toTerm(state));
        result.addMember(state, mRhs.toTerm(state));
        return result;
    }

    public static ProductAssignment termToStatement(final Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr lhs = TermConverter.valueToExpr(term.firstMember());
            final Expr rhs = TermConverter.valueToExpr(PRECEDENCE, false, term.lastMember());
            return new ProductAssignment(lhs, rhs);
        }
    }

}

