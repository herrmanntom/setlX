package org.randoom.setlx.functions;

import org.randoom.setlx.utilities.MathFunction2;
import java.lang.reflect.Method;

/**
 * hypot(numberValue, numberValue) : Returns sqrt(x2 +y2) without intermediate overflow or underflow.
 */
public class PD_hypot extends MathFunction2 {
    /** Definition of the PreDefinedProcedure `hypot'. */
    public final static PreDefinedProcedure DEFINITION;
    static {
        Method function;
        try {
            function = Math.class.getDeclaredMethod("hypot", double.class, double.class);
        } catch (final Exception e) {
            // will not happen
            function = null;
        }
        DEFINITION = new PD_hypot(function);
    }

    private PD_hypot(final Method function) {
        super("hypot", function);
    }
}

