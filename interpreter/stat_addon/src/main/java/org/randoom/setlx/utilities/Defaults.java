package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.SetlDouble;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides default values for the statistical distribution functions.
 */
public class Defaults {

    public static SetlDouble getDefaultPlotInterval() {
        return createSetlDoubleValue(0.01);
    }

    public static SetlDouble getDefaultLowerBoundOfNegativeFive() {
        return createSetlDoubleValue(-5.0);
    }

    public static SetlDouble getDefaultUpperBoundOfFive() {
        return createSetlDoubleValue(5.0);
    }

    public static SetlDouble getDefaultLowerBoundOfZero() {
        return createSetlDoubleValue(0.0);
    }

    public static SetlDouble getDefaultUpperBoundOfTen() {
        return createSetlDoubleValue(10.0);
    }

    /** Default graph color scheme (black) */
    public static List<Integer> DEFAULT_COLOR_SCHEME;
    static {
        DEFAULT_COLOR_SCHEME = new ArrayList<>();
        DEFAULT_COLOR_SCHEME.add(0);
        DEFAULT_COLOR_SCHEME.add(0);
        DEFAULT_COLOR_SCHEME.add(0);
    }

    private static SetlDouble createSetlDoubleValue(double value) {
        try {
            return SetlDouble.valueOf(value);
        } catch (UndefinedOperationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
