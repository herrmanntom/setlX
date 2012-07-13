package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.JVMException;
import org.randoom.setlx.exceptions.NumberToLargeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.functions.PreDefinedFunction;
import org.randoom.setlx.types.NumberValue;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Real;
import org.randoom.setlx.types.Value;

import java.lang.reflect.Method;
import java.util.List;

// this class encapsulates functions from java.Math

public class MathFunction extends PreDefinedFunction {
    private Method mFunction;

    public MathFunction(final String name, final Method function) {
        super(name);
        addParameter("x");
        mFunction = function;
    }

    public Real execute(final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        if (!(args.get(0) instanceof NumberValue)) {
            throw new IncompatibleTypeException(
                "This function requires a single number as parameter."
            );
        }
        final Value arg = args.get(0).toReal();
        if (arg != Om.OM) {
            try {
                Object result = mFunction.invoke(null, ((Real) arg).doubleValue());
                return new Real(result.toString());
            } catch (NumberFormatException nfe) {
                throw new NumberToLargeException(
                    "Involved numbers are to large or to small for this operation."
                );
            } catch (SetlException se) {
                throw se;
            } catch (Exception e) {
                throw new JVMException(
                    "Error during calling a predefined mathematical function.\n" +
                    "This is probably a bug in the interpreter.\n" +
                    "Please report it including executed source example."
                );
            }
        } else {
            throw new IncompatibleTypeException(
                "This function requires a single number as parameter."
            );
        }
    }
}

