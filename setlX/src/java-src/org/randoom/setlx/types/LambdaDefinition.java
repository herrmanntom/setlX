package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.statements.Return;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
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

    private Expr mExpr; // expression in the body of the definition; used only for toString() and toTerm()

    public LambdaDefinition(List<ParameterDef> parameters, Expr expr) {
        super(parameters, new Block(1));
        mExpr = expr;
        mStatements.add(new Return(mExpr));
    }

    /* string and char operations */

    public String toString(int tabs) {
        String result = "";
        if (mParameters.size() == 1) {
            result += mParameters.get(0);
        } else {
            result += mParameters;
        }
        result += " |-> " + mExpr.toString(tabs);
        return result;
    }

    /* term operations */

    public Value toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);

        SetlList paramList = new SetlList();
        for (ParameterDef param: mParameters) {
            paramList.addMember(param.toTerm());
        }
        result.addMember(paramList);

        result.addMember(mExpr.toTerm());

        return result;
    }

    public static LambdaDefinition termToValue(Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            SetlList            paramList   = (SetlList) term.firstMember();
            List<ParameterDef>  parameters  = new ArrayList<ParameterDef>(paramList.size());
            for (Value v : paramList) {
                parameters.add(ParameterDef.valueToParameterDef(v));
            }
            Expr                expr        = TermConverter.valueToExpr(term.lastMember());
            return new LambdaDefinition(parameters, expr);
        }
    }
}

