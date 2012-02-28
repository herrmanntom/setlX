package interpreter.statements;

import interpreter.exceptions.ReturnException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.expressions.Expr;
import interpreter.types.Om;
import interpreter.types.SetlString;
import interpreter.types.Term;
import interpreter.utilities.Environment;
import interpreter.utilities.TermConverter;

/*
grammar rule:
statement
    : [...]
    | 'return' anyExpr? ';'
    ;

implemented here as:
               =======
               mResult
*/

public class Return extends Statement {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "'return";

    private Expr mResult;

    public Return(Expr result) {
        mResult = result;
    }

    public void execute() throws SetlException {
        if (mResult != null) {
            throw new ReturnException(mResult.eval());
        } else {
            throw new ReturnException(Om.OM);
        }
    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs) + "return";
        if (mResult != null){
            result += " " + mResult;
        }
        result += ";";
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term(FUNCTIONAL_CHARACTER);
        if (mResult != null) {
            result.addMember(mResult.toTerm());
        } else {
            result.addMember(new SetlString("nil"));
        }
        return result;
    }

    public static Return termToStatement(Term term) throws TermConversionException {
        if (term.size() != 1) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr expr = null;
            if (! term.firstMember().equals(new SetlString("nil"))) {
                expr = TermConverter.valueToExpr(term.firstMember());
            }
            return new Return(expr);
        }
    }
}

