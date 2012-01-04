package interpreter.expressions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.types.Term;
import interpreter.types.Value;

import java.util.ArrayList;
import java.util.List;

/*
grammar rule:
term
    : TERM '(' termArguments ')'
    ;

implemented here as:
      ====     ==============
      mID          mArgs
*/

public class TermConstructor extends Expr {
    private Variable   mId;        // functional character of the term
    private List<Expr> mArgs;      // list of arguments

    public TermConstructor(Variable id, List<Expr> args) {
        mId     = id;
        mArgs   = args;
    }

    public Term evaluate() throws SetlException {
        Value   r       = mId.eval();
        Term    result  = null;
        if (r instanceof Term) {
            result  = (Term) r;
        } else { // this should not happen if Variable is implemented correctly
            throw new IncompatibleTypeException("Identifier \"" + mId + "\" is not a term.");
        }

        for (Expr arg: mArgs) {
            result.addMember(arg.eval());
        }

        return result;
    }

    /* string operations */

    public String toString(int tabs) {
        String result = mId + "(";
        for (int i = 0; i < mArgs.size(); ++i) {
            if (i > 0) {
                result += ", ";
            }
            result += mArgs.get(i).toString(tabs);
        }
        result += ")";
        return result;
    }

    /* term operations */

    public Term toTerm() throws SetlException {
        return evaluate();
    }
}

