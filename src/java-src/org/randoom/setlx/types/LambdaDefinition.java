package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.statements.Return;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

// This class represents a function definition

/*
grammar rule:
lambdaDefinition
    : lambdaParameters    '|->' sum
    ;

implemented here as:
      ----------------          ===
   mParameters (inherited)     mExpr
*/

public class LambdaDefinition extends ProcedureDefinition {
    // functional character used in terms
    public  final static String FUNCTIONAL_CHARACTER = "^lambdaProcedure";

    private final Expr mExpr; // expression in the body of the definition; used directly only for toString() and toTerm()

    public LambdaDefinition(final List<ParameterDef> parameters, final Expr expr) {
        super(parameters, new Block(1));
        mExpr = expr;
        mStatements.add(new Return(mExpr));
    }
    protected LambdaDefinition(
        final List<ParameterDef>       parameters,
        final Block                    statements,
        final HashMap<Variable, Value> closure,
        final Expr                     expr
    ) {
        super(parameters, statements, closure);
        mExpr = expr;
    }

    @Override
    public LambdaDefinition createCopy() {
        return new LambdaDefinition(mParameters, mExpr);
    }

    @Override
    public LambdaDefinition clone() {
        if (mClosure != null || mObject != null) {
            return new LambdaDefinition(mParameters, mStatements, mClosure, mExpr);
        } else {
            return this;
        }
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        mObject = null;
        if (mParameters.size() == 1) {
            mParameters.get(0).appendString(state, sb, 0);
        } else {
            sb.append("[");
            final Iterator<ParameterDef> iter = mParameters.iterator();
            while (iter.hasNext()) {
                iter.next().appendString(state, sb, 0);
                if (iter.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        }
        sb.append(" |-> ");
        mExpr.appendString(state, sb, tabs);
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        mObject = null;
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        final SetlList paramList = new SetlList(mParameters.size());
        for (final ParameterDef param: mParameters) {
            paramList.addMember(state, param.toTerm(state));
        }
        result.addMember(state, paramList);

        result.addMember(state, mExpr.toTerm(state));

        return result;
    }

    public static LambdaDefinition termToValue(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList            paramList   = (SetlList) term.firstMember();
            final List<ParameterDef>  parameters  = new ArrayList<ParameterDef>(paramList.size());
            for (final Value v : paramList) {
                parameters.add(ParameterDef.valueToParameterDef(v));
            }
            final Expr                expr        = TermConverter.valueToExpr(term.lastMember());
            return new LambdaDefinition(parameters, expr);
        }
    }

    private final static int initHashCode = LambdaDefinition.class.hashCode();

    @Override
    public int hashCode() {
        mObject = null;
        return initHashCode;
    }
}

