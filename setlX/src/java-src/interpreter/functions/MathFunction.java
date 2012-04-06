package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.JVMException;
import interpreter.exceptions.NumberToLargeException;
import interpreter.exceptions.SetlException;
import interpreter.types.NumberValue;
import interpreter.types.Om;
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
        Value  arg      = args.get(0).toReal();
        if (arg != Om.OM) {
            try {
                Object result   = mFunction.invoke(null, ((Real) arg).doubleValue());
                return new Real(new Double(result.toString()));
            } catch (NumberFormatException nfe) {
                throw new NumberToLargeException("Involved numbers are to large or to small for this operation.");
            } catch (SetlException se) {
                throw se;
            } catch (Exception e) {
                throw new JVMException("Error during calling a predefined mathematical function.\n"
                                     + "This is probably a bug in the interpreter.\n"
                                     + "Please report it including executed source example.");
            }
        } else {
            throw new IncompatibleTypeException("This function requires a single number as parameter.");
        }
    }
}

