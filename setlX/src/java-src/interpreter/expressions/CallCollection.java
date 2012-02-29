package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.exceptions.UnknownFunctionException;
import interpreter.types.Om;
import interpreter.types.ProcedureDefinition;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.TermConverter;

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
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "'callCollection";

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

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mLhs.toTerm());
        result.addMember(mArg.toTerm());
        return result;
    }

    public static CallCollection termToExpr(Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr lhs = TermConverter.valueToExpr(term.firstMember());
            Expr arg = TermConverter.valueToExpr(term.lastMember());
            return new CallCollection(lhs, arg);
        }
    }
}

