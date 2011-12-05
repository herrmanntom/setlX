package interpreter.expressions;

import interpreter.exceptions.SetlException;
import interpreter.types.Om;
import interpreter.types.Value;
import interpreter.utilities.VariableScope;

public class Variable extends Expr {
    private String mId;

    public Variable(String id) {
        mId = id;
    }

    public Value evaluate() throws SetlException {
        Value v = VariableScope.findValue(mId);
        if (v == null){
            return Om.OM;
        }else{
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

    public String toString(int tabs) {
        return mId;
    }
}
