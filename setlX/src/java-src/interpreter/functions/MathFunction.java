package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.JVMException;
import interpreter.exceptions.NumberToLargeException;
import interpreter.exceptions.SetlException;
import interpreter.types.NumberValue;
import interpreter.types.Real;
import interpreter.types.Value;

import java.lang.reflect.Method;
import java.util.List;

// this class encapsulates functions from java.Math

public class MathFunction extends PreDefinedFunction {
    private Method mFunction;

    public MathFunction(String name, Method function) {
        super(name);
        addParameter("x");
        mFunction = function;
    }

    public Real execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        if (!(args.get(0) instanceof NumberValue)) {
            throw new IncompatibleTypeException("This function requires a single number as parameter.");
        }
        try {
            Object result = mFunction.invoke(null, new Double(args.get(0).toString()));
            return new Real(new Double(result.toString()));
        } catch (NumberFormatException nfe) {
            throw new NumberToLargeException("'" + args.get(0) + "' is to large for this operation.");
        } catch (Exception e) {
            throw new JVMException("Error during calling a predefined mathematical function.\n"
                                 + "This is probably a bug in the interpreter.\n"
                                 + "Please report it including executed source example.");
        }
    }
}

