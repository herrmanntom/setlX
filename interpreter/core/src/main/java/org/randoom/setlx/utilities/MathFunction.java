package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.JVMException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.functions.PreDefinedProcedure;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.SetlObject;
import org.randoom.setlx.types.Value;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Objects of this class encapsulate functions from java.Math using a single
 * double as argument.
 */
public class MathFunction extends PreDefinedProcedure {
    private final        Method       function;

    private final static ParameterDefinition X        = createParameter("x");

    /**
     * Encapsulate a java.Math function.
     *
     * @param name     Name of the function.
     * @param function Function to encapsulate.
     */
    public MathFunction(final String name, final Method function) {
        super();
        setName(name);
        addParameter(X);
        this.function = function;
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value arg = args.get(X);
        if (arg.isNumber() == SetlBoolean.TRUE) {
            try {
                final double r = (Double) function.invoke(null, arg.toJDoubleValue(state));
                return SetlDouble.valueOf(r);
            } catch (final SetlException se) {
                throw se;
            } catch (final Exception e) {
                throw new JVMException(
                    "Error during calling a predefined mathematical function.\n" +
                    "This is probably a bug in the interpreter.\n" +
                    "Please report it including executed source example.",
                    e
                );
            }
        } else if (arg.getClass() == SetlObject.class) {
            return ((SetlObject) arg).overloadMathFunction(state, getName());
        }

        throw new IncompatibleTypeException(
            "This function requires a single number as parameter."
        );
    }
}

