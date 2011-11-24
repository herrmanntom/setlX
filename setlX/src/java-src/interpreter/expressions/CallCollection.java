package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.exceptions.UnknownFunctionException;
import interpreter.types.Om;
import interpreter.types.ProcedureDefinition;
import interpreter.types.Value;

import java.util.List;

public class CallCollection extends Expr {
    private Expr       mLhs;      // left hand side (function name, variable, other call, etc)
    private Expr       mArg;      // argument

    public CallCollection(Expr lhs, Expr arg) {
        mLhs   = lhs;
        mArg   = arg;
    }

    public Value evaluate() throws SetlException {
        Value lhs = mLhs.eval();
        if (lhs == Om.OM) {
            throw new UnknownFunctionException("\"" + mLhs + "\" is undefined.");
        }
        if (lhs instanceof ProcedureDefinition) {
            throw new UndefinedOperationException("Incorrect set of brackets for function call.");
        }
        return lhs.callCollection(mArg.eval().clone());
    }

    public String toString(int tabs) {
        String result = mLhs.toString(tabs) + "{";
        result += mArg.toString(tabs);
        result += "}";
        return result;
    }
}

