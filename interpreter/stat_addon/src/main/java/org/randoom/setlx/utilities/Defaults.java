package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Provides default values for the statistical distribution functions.
 */
public class Defaults {

    /** Default plot interval of 0.01 **/
    public static SetlDouble getDefaultPlotInterval() {
        return createSetlDoubleValue(0.01);
    }

    public static SetlDouble createSetlDoubleValue(double value) {
        try {
            return SetlDouble.valueOf(value);
        } catch (UndefinedOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Integer> createColorScheme(Value color, State state) throws SetlException {
        List<Integer> colorScheme = new ArrayList<>();

        if (color.isString() == SetlBoolean.TRUE && color.toString().equals("DEFAULT_COLOR")) {
            colorScheme.add(0);
            colorScheme.add(0);
            colorScheme.add(0);
        } else {
            for (Iterator<Value> value = ((SetlList) color).iterator(); value.hasNext();) {
                colorScheme.add(value.next().toJIntValue(state));
            }
        }
        return colorScheme;
    }
}
