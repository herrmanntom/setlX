package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.statements.Return;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
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

    /* string and char operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        if (mParameters.size() == 1) {
            mParameters.get(0).appendString(sb);
        } else {
            sb.append("[");
            final Iterator<ParameterDef> iter = mParameters.iterator();
            while (iter.hasNext()) {
                iter.next().appendString(sb);
                if (iter.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        }
        sb.append(" |-> ");
        mExpr.appendString(sb, tabs);
    }

    /* term operations */

    public Value toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        final SetlList paramList = new SetlList(mParameters.size());
        for (ParameterDef param: mParameters) {
            paramList.addMember(param.toTerm());
        }
        result.addMember(paramList);

        result.addMember(mExpr.toTerm());

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
}

