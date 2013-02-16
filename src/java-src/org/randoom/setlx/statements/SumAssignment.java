package org.randoom.setlx.statements;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.AssignableExpression;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ReturnMessage;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/*
grammar rule:
assignmentOther
    : assignable ('+=' | [...] ) anyExpr
    ;

implemented here as:
      ==========                 =======
         mLhs                     mRhs
*/

public class SumAssignment extends StatementWithPrintableResult {
    // functional character used in terms
    public  final static String     FUNCTIONAL_CHARACTER    = "^sumAssignment";

    // precedence level in SetlX-grammar
    private final static int        PRECEDENCE              = 1000;

    private final AssignableExpression lhs;
    private final Expr                 rhs;
    private       boolean              printAfterEval;

    public SumAssignment(final AssignableExpression lhs, final Expr rhs) {
        this.lhs            = lhs;
        this.rhs            = rhs;
        this.printAfterEval = false;
    }

    /*package*/ @Override
    void setPrintAfterEval() {
        printAfterEval = true;
    }

    @Override
    protected ReturnMessage execute(final State state) throws SetlException {
        final Value assigned = lhs.eval(state).sumAssign(state, rhs.eval(state).clone());
        lhs.assignUncloned(state, assigned);

        if (state.traceAssignments) {
            state.outWriteLn("~< Trace: " + lhs + " := " + assigned + " >~");
        } else if (printAfterEval) {
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
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        // first we evaluate lhs and rhs as usual
        lhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        rhs.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);

        // then assign to lhs
        lhs.collectVariablesWhenAssigned(boundVariables, boundVariables, boundVariables);
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        state.getLineStart(sb, tabs);
        lhs.appendString(state, sb, tabs);
        sb.append(" += ");
        rhs.appendString(state, sb, tabs);
        sb.append(";");
    }

    /* term operations */

    @Override
    public Term toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);
        result.addMember(state, lhs.toTerm(state));
        result.addMember(state, rhs.toTerm(state));
        return result;
    }

    public static SumAssignment termToStatement(final Term term) throws TermConversionException {
        if (term.size() == 2) {
            final Expr lhs = TermConverter.valueToExpr(term.firstMember());
            final Expr rhs = TermConverter.valueToExpr(PRECEDENCE, false, term.lastMember());
            if (lhs instanceof AssignableExpression) {
                return new SumAssignment((AssignableExpression) lhs, rhs);
            }
        }
        throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
    }

}

