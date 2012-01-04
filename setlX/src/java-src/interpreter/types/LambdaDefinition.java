package interpreter.types;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.statements.Block;
import interpreter.statements.Return;
import interpreter.utilities.ParameterDef;

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
    private Expr mExpr; // expression in the body of the definition; used only for toString() and toTerm()

    public LambdaDefinition(List<ParameterDef> parameters, Expr expr) {
        super(parameters, new Block());
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

    public Value toTerm() throws SetlException {
        Term result = new Term("'lambdaProcedure");

        SetlList paramList = new SetlList();
        for (ParameterDef param: mParameters) {
            paramList.addMember(param.toTerm());
        }
        result.addMember(paramList);

        result.addMember(mExpr.toTerm());

        return result;
    }
}

