package interpreter.expressions;

import interpreter.exceptions.SetlException;
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
    private String mId;

    public Variable(String id) {
        mId = id;
    }

    public Value evaluate() throws SetlException {
        // user wants a term when fist char is upper case or single qoute ( ' )
        if (mId.length() > 0 && (mId.charAt(0) == '\'' || Character.isUpperCase(mId.charAt(0)))) {
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
        Term result = new Term("'variable");
        result.addMember(new SetlString(mId));
        return result;
    }
}

