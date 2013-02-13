package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.JVMException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.functions.PreDefinedFunction;
import org.randoom.setlx.types.NumberValue;
import org.randoom.setlx.types.Real;
import org.randoom.setlx.types.SetlObject;
import org.randoom.setlx.types.Value;

import java.lang.reflect.Method;
import java.util.List;

// this class encapsulates functions from java.Math

public class MathFunction2 extends PreDefinedFunction {
    private final Method mFunction;

    public MathFunction2(final String name, final Method function) {
        super(name);
        addParameter("x");
        addParameter("y");
        mFunction = function;
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        if ( ! (args.get(0) instanceof NumberValue || args.get(0) instanceof SetlObject) ||
             ! (args.get(1) instanceof NumberValue || args.get(1) instanceof SetlObject)
        ) {
            throw new IncompatibleTypeException(
                "This function requires two numbers as parameters."
            );
        }
        try {
            final double r = (Double) mFunction.invoke(null, args.get(0).toJDoubleValue(state), args.get(1).toJDoubleValue(state));
            return Real.valueOf(r);
        } catch (final SetlException se) {
            throw se;
        } catch (final Exception e) {
            throw new JVMException(
                "Error during calling a predefined mathematical function.\n" +
                "This is probably a bug in the interpreter.\n" +
                "Please report it including executed source example."
            );
        }
    }
}

