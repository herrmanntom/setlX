package interpreter.expressions;

import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.Term;
import interpreter.types.IgnoreDummy;
import interpreter.utilities.Environment;

/*
grammar rules:
assignable
    : variable   | idList      | '_'
    ;

value
    : list | set | atomicValue | '_'
    ;

this class implements an ignored variable inside an idList or expression:
                                 ===
*/

public class VariableIgnore extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    public  final static String         FUNCTIONAL_CHARACTER = "'variableIgnore";
    // precedence level in SetlX-grammar
    private final static int            PRECEDENCE           = 9999;

    public  final static VariableIgnore VI                   = new VariableIgnore();

    private              int            mLineNr;

    private VariableIgnore() {
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
    }

    public IgnoreDummy evaluate() throws UndefinedOperationException {
        return IgnoreDummy.ID;
    }

    /* string operations */

    public String toString(int tabs) {
        return "_";
    }

    /* term operations */

    public Term toTerm() {
        return new Term(FUNCTIONAL_CHARACTER);
    }

    public static VariableIgnore termToExpr(Term term) {
        return VI;
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}

