package interpreter.types;

import interpreter.expressions.Expr;
import interpreter.statements.Block;
import interpreter.statements.Return;
import interpreter.utilities.ParameterDef;

import java.util.List;

// This class represents a function definition
public class LambdaDefinition extends SetlDefinition {
    private Expr mExpr; // expression in the body of the definition; used only for toString()

    public LambdaDefinition(List<ParameterDef> parameters, Expr expr) {
        super(parameters, new Block());
        mExpr = expr;
        mStatements.add(new Return(mExpr));
    }

    public String toString(int tabs) {
        String result = "(";
        for (int i = 0; i < mParameters.size(); ++i) {
            if (i > 0) {
                result += ", ";
            }
            result += mParameters.get(i);
        }
        result += ")$(" + mExpr.toString(tabs) + ")";
        return result;
    }
}

