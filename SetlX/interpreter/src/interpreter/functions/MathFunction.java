package interpreter.functions;

import interpreter.exceptions.IncorrectNumberOfParametersException;
import interpreter.exceptions.JVMException;
import interpreter.exceptions.NumberToLargeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.NumberValue;
import interpreter.types.SetlReal;
import interpreter.types.Value;

import java.lang.reflect.Method;
import java.util.List;

public class MathFunction extends PreDefinedFunction {
    private Method mFunction;

    public MathFunction(String name, Method function) {
        super(name);
        mFunction = function;
    }

    public SetlReal call(List<Value> args, boolean returnCollection) throws SetlException {
        if (returnCollection) {
            throw new UndefinedOperationException("Incorrect set of brackets for function call.");
        }
        if (args.size() != 1 || !(args.get(0) instanceof NumberValue)) {
            throw new IncorrectNumberOfParametersException("This function requires a single number as parameter.");
        }
        try {
            Object result = mFunction.invoke(null, new Double(args.get(0).toString()));
            return new SetlReal(new Double(result.toString()));
        } catch (NumberFormatException nfe) {
            throw new NumberToLargeException("`" + args.get(0) + "´ is to large for this operation.");
        } catch (Exception e) {
            throw new JVMException("Error during calling a predefined mathematical function.\n"
                                 + "This is probably a bug in the interpreter.\n"
                                 + "Please report it including executed source example.");
        }
    }

    public boolean writeVars() {
        return false;
    }
}
