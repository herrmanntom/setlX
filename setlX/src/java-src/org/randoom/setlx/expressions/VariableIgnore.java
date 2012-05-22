package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.IgnoreDummy;

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
    public  final static String         FUNCTIONAL_CHARACTER = "^variableIgnore";
    // precedence level in SetlX-grammar
    private final static int            PRECEDENCE           = 9999;

    public  final static VariableIgnore VI                   = new VariableIgnore();

    private VariableIgnore() { }

    protected IgnoreDummy evaluate() throws UndefinedOperationException {
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

