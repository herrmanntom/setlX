package interpreter.expressions;

import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.Term;
import interpreter.types.IgnoreDummy;

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

    public final static VariableIgnore VI = new VariableIgnore();

    private VariableIgnore() {}

    public IgnoreDummy evaluate() throws UndefinedOperationException {
        return IgnoreDummy.ID;
    }

    /* string operations */

    public String toString(int tabs) {
        return "_";
    }

    /* term operations */

    public Term toTerm() {
        return new Term("'variableIgnore");
    }
}

