package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.exceptions.UnknownFunctionException;
import interpreter.types.Om;
import interpreter.types.ProcedureDefinition;
import interpreter.types.SetlList;
import interpreter.types.Term;
import interpreter.types.Value;

import java.util.List;

/*
grammar rule:
call
    : varOrTerm ('(' callParameters ')' | '{' anyExpr '}')*
    ;

implemented here as:
      =========                               =======
         mLhs                                   mArg
*/

public class CallCollection extends Expr {
    private Expr    mLhs;      // left hand side (function name, variable, other call, etc)
    private Expr    mArg;      // argument

    public CallCollection(Expr lhs, Expr arg) {
        mLhs   = lhs;
        mArg   = arg;
    }

    public Value evaluate() throws SetlException {
        Value lhs = mLhs.eval();
        if (lhs == Om.OM) {
            throw new UnknownFunctionException("\"" + mLhs + "\" is undefined.");
        } else if (lhs instanceof ProcedureDefinition) {
            throw new UndefinedOperationException("Incorrect set of brackets for function call.");
        } else if (lhs instanceof Term) {
            throw new UndefinedOperationException("Incorrect set of brackets for term.");
        }
        return lhs.callCollection(mArg.eval().clone());
    }

    /* string operations */

    public String toString(int tabs) {
        String result = mLhs.toString(tabs) + "{";
        result += mArg.toString(tabs);
        result += "}";
        return result;
    }

    /* term operations */

    public Term toTerm() throws SetlException {
        Term        result      = new Term("'collectionCall");
        SetlList    arguments   = new SetlList();
        result.addMember(mLhs.toTerm());
        result.addMember(arguments);
        arguments.addMember(mArg.toTerm());
        return result;
    }
}

