package interpreter.expressions;

import interpreter.Environment;
import interpreter.exceptions.SetlException;
import interpreter.types.SetlOm;
import interpreter.types.Value;

public class Variable extends Expr {
    private String mId;

    public Variable(String id) {
        mId             = id;
    }

    public String getId() {
        return mId;
    }

    public Value evaluate() throws SetlException {
        Value v = Environment.findValue(mId);
        if (v == null){
            return SetlOm.OM;
        }else{
            return v;
        }
    }

    public String toString() {
        return this.getId();
    }
}
