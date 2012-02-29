package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.TermConverter;

//  grammar rule:
//  prefixOperation
//      : '*/' factor
//      | [...]
//      ;
//
//  implemented here as:
//             ======
//             mExpr

public class MultiplyMembers extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "'multiplyMembers";

    private Expr mExpr;

    public MultiplyMembers(Expr expr) {
        mExpr = expr;
    }

    public Value evaluate() throws SetlException {
        return mExpr.eval().multiplyMembers();
    }

    /* string operations */

    public String toString(int tabs) {
        return "*/" + mExpr.toString(tabs);
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(mExpr.toTerm());
        return result;
    }

    public static MultiplyMembers termToExpr(Term term) throws TermConversionException {
        if (term.size() != 1) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr expr = TermConverter.valueToExpr(term.firstMember());
            return new MultiplyMembers(expr);
        }
    }
}

