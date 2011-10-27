package interpreter.utilities;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Variable;
import interpreter.types.Value;

// This class represents a single parameter of a function definition
public class ParameterDef {
    public final static int READ_ONLY   = 0;
    public final static int READ_WRITE  = 1;

    private Variable mVar;
    private int      mType;

    public ParameterDef(Variable var, int type) {
        mVar  = var;
        mType = type;
    }

    public ParameterDef(String id, int type) {
        this(new Variable(id), type);
    }

    public ParameterDef(Variable var) {
        this(var, READ_ONLY);
    }

    public ParameterDef(String id) {
        this(new Variable(id), READ_ONLY);
    }

    public void assign(Value v) throws SetlException {
        mVar.assign(v);
    }

    public Value getValue() throws SetlException {
        return mVar.eval();
    }

    public int getType() {
        return mType;
    }

    public String toString() {
        String result = "";
        if (mType == READ_WRITE) {
            result += "rw ";
        }
        result += mVar;
        return result;
    }
}

