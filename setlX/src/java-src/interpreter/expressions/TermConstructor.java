package interpreter.expressions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.Environment;
import interpreter.utilities.TermConverter;

import java.util.ArrayList;
import java.util.List;

/*
grammar rule:
term
    : TERM '(' termArguments ')'
    ;

implemented here as:
      ====     ==============
     mFChar        mArgs
*/

public class TermConstructor extends Expr {
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 9999;

    private String     mFChar;     // functional character of the term
    private List<Expr> mArgs;      // list of arguments
    private int        mLineNr;

    public TermConstructor(String fChar, List<Expr> args) {
        mFChar  = fChar;
        mArgs   = args;
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
        for (Expr arg : mArgs) {
            arg.computeLineNr();
        }
    }

    public Term evaluate() throws SetlException {
        Term    result  = new Term(mFChar);

        for (Expr arg: mArgs) {
            result.addMember(arg.eval().toTerm()); // evaluate arguments at runtime
        }

        return result;
    }

    /* string operations */

    public String toString(int tabs) {
        String result = mFChar + "(";
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

    public Term toTerm() {
        Term result = new Term(mFChar);

        for (Expr arg: mArgs) {
            result.addMember(arg.toTerm()); // do not evaluate here
        }

        return result;
    }

    public Term toTermQuoted() throws SetlException {
        return this.evaluate();
    }

    public static Expr termToExpr(Term term) {
        String      functionalCharacter = term.functionalCharacter().getUnquotedString();
        List<Expr>  args                = new ArrayList<Expr>(term.size());
        for (Value v : term) {
            args.add(TermConverter.valueToExpr(v));
        }
        return new TermConstructor(functionalCharacter, args);
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}

