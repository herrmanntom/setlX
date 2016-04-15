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
 * Objects of this class encapsulate functions from java.Math using two
 * doubles as arguments.
 */
public class MathFunction2 extends PreDefinedProcedure {
    private final        Method       function;

    private final static ParameterDefinition X        = createParameter("x");
    private final static ParameterDefinition Y        = createParameter("y");

    /**
     * Encapsulate a java.Math function.
     *
     * @param name     Name of the function.
     * @param function Function to encapsulate.
     */
    public MathFunction2(final String name, final Method function) {
        super();
        setName(name);
        addParameter(X);
        addParameter(Y);
        this.function = function;
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value arg0 = args.get(X);
        final Value arg1 = args.get(Y);
        if (arg0.isNumber() == SetlBoolean.TRUE && arg1.isNumber() == SetlBoolean.TRUE) {
            try {
                final double r = (Double) function.invoke(null, arg0.toJDoubleValue(state), arg1.toJDoubleValue(state));
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
        } else if (arg0.getClass() == SetlObject.class && arg1.getClass() == SetlObject.class) {
            return ((SetlObject) arg0).overloadMathFunction(state, getName(), arg1);
        }

        throw new IncompatibleTypeException(
            "This function requires two numbers as parameters."
        );
    }
}

