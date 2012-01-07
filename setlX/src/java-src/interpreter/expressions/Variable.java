package interpreter.expressions;

import interpreter.types.Om;
import interpreter.types.SetlString;
import interpreter.types.Term;
import interpreter.types.Value;
import interpreter.utilities.VariableScope;

/*
grammar rule:
variable
    : ID
    ;

implemented here as:
      ==
      mId
*/

public class Variable extends Expr {
    private String  mId;
    private boolean isTerm;

    public  final static String FUNCTIONAL_CHARACTER = "'variable";

    public Variable(String id) {
        mId     = id;
        isTerm  = (id.length() > 0 && (id.charAt(0) == '\'' || Character.isUpperCase(id.charAt(0))));
    }

    public Value evaluate() {
        if (isTerm) {
            return new Term(mId);
        }

        Value v = VariableScope.findValue(mId);
        if (v == null) {
            return Om.OM;
        } else {
            return v;
        }
    }

    // sets this expression to the given value
    public void assign(Value v) {
        VariableScope.putValue(mId, v.clone());
    }

    // sets this expression to the given value
    public void makeGlobal() {
        VariableScope.makeGlobal(mId);
    }

    /* string operations */

    public String toString(int tabs) {
        return mId;
    }

    /* term operations */

    public Term toTerm() {
        if (isTerm) {
            return new Term(mId);
        }

        Term result = new Term(FUNCTIONAL_CHARACTER);
        result.addMember(new SetlString(mId));
        return result;
    }
}

