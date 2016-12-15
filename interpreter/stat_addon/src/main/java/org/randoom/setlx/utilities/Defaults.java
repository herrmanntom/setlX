package org.randoom.setlx.utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides default values for the statistical distribution functions.
 */
public class Defaults {

    /** Default graph color scheme (black) */
    public static List<Integer> DEFAULT_COLOR_SCHEME;
    static {
        DEFAULT_COLOR_SCHEME = new ArrayList<>();
        DEFAULT_COLOR_SCHEME.add(0);
        DEFAULT_COLOR_SCHEME.add(0);
        DEFAULT_COLOR_SCHEME.add(0);
    }
}
