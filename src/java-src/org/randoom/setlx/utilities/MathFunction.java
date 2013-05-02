package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.JVMException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.functions.PreDefinedProcedure;
import org.randoom.setlx.types.NumberValue;
import org.randoom.setlx.types.Real;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.SetlObject;
import org.randoom.setlx.types.Value;

import java.lang.reflect.Method;
import java.util.List;

// this class encapsulates functions from java.Math

public class MathFunction extends PreDefinedProcedure {
    private final Method function;

    public MathFunction(final String name, final Method function) {
        super();
        setName(name);
        addParameter("x");
        this.function = function;
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        final Value arg = args.get(0);
        if (arg instanceof NumberValue) {
            try {
                final double r = (Double) function.invoke(null, arg.jDoubleValue());
                return SetlDouble.valueOf(r);
            } catch (final SetlException se) {
                throw se;
            } catch (final Exception e) {
                throw new JVMException(
                    "Error during calling a predefined mathematical function.\n" +
                    "This is probably a bug in the interpreter.\n" +
                    "Please report it including executed source example."
                );
            }
        } else if (arg instanceof SetlObject) {
            return ((SetlObject) arg).overloadMathFunction(state, getName());
        }

        throw new IncompatibleTypeException(
            "This function requires a single number as parameter."
        );
    }
}

