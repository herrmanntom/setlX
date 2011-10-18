package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.statements.Block;
import interpreter.types.SetlDefinition;
import interpreter.types.Value;
import interpreter.utilities.Environment;

import java.util.LinkedList;
import java.util.List;

public abstract class PreDefinedFunction extends SetlDefinition {

    private String mName;

    protected PreDefinedFunction(String name) {
        super(new LinkedList<String>(), new Block());
        mName = name;
    }

    protected PreDefinedFunction(String name, List<String> parameters) {
        super(parameters, new Block());
        mName = name;
    }

    public final String getName() {
        return mName;
    }

    public abstract Value   call(List<Value> args) throws SetlException;

    public abstract boolean writeVars();

    public final String toString(int tabs) {
        String endl = Environment.getEndl();
        String result = "procedure (";
        for (int i = 0; i < mParameters.size(); ++i) {
            if (i > 0) {
                result += ", ";
            }
            result += mParameters.get(i);
        }
        result += ") {" + endl;
        result += Environment.getTabs(tabs + 1) + "// predefined procedure `" + mName + "'";
        result += Environment.getTabs(tabs) + "}";
        return result;
    }

}

