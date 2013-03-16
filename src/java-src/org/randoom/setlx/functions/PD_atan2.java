package org.randoom.setlx.functions;

import org.randoom.setlx.utilities.MathFunction2;
import java.lang.reflect.Method;

/*
 * atan2(numberValue, numberValue) : Returns the angle theta from the conversion of rectangular coordinates (x, y) to polar coordinates (r, theta).
 */

public class PD_atan2 extends MathFunction2 {
    public final static PreDefinedProcedure DEFINITION;

    static {
        Method function;
        try {
            function = Math.class.getDeclaredMethod("atan2", double.class, double.class);
        } catch (final Exception e) {
            // will not happen
            function = null;
        }
        DEFINITION = new PD_atan2(function);
    }

    private PD_atan2(final Method function) {
        super("atan2", function);
    }
}

