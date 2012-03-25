package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.exceptions.TermConversionException;
import interpreter.exceptions.UnknownFunctionException;
import interpreter.types.Om;
import interpreter.types.ProcedureDefinition;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.Environment;
import interpreter.utilities.TermConverter;

/*
grammar rule:
call
    : variable ('(' callParameters ')')? ('[' collectionAccessParams ']' | '{' anyExpr '}')*
    ;

implemented here as:
      ========                                                                 =======
        mLhs                                                                     mArg
*/

public class CollectMap extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^collectMap";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1900;

    private Expr    mLhs;      // left hand side (Variable, other CollectMap, CollectionAccess, etc)
    private Expr    mArg;      // argument
    private int     mLineNr;

    public CollectMap(Expr lhs, Expr arg) {
        mLhs    = lhs;
        mArg    = arg;
        mLineNr = -1;
    }

    public int getLineNr() {
        if (mLineNr < 0) {
            computeLineNr();
        }
        return mLineNr;
    }

    public void computeLineNr() {
        mLineNr = Environment.sourceLine;
        mLhs.computeLineNr();
        mArg.computeLineNr();
    }

    public Value evaluate() throws SetlException {
        Value lhs = mLhs.eval();
        if (lhs == Om.OM) {
            throw new UnknownFunctionException("\"" + mLhs + "\" is undefined.");
        }
        return lhs.collectMap(mArg.eval().clone());
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

    public static CollectMap termToExpr(Term term) throws TermConversionException {
        if (term.size() != 2) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            Expr lhs = TermConverter.valueToExpr(term.firstMember());
            Expr arg = TermConverter.valueToExpr(term.lastMember());
            return new CollectMap(lhs, arg);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}

