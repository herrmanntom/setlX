package org.randoom.setlx.plot.types;

import org.randoom.setlx.types.Value;

import java.util.List;
import java.util.Objects;

public class Chart2D extends Chart<List<Double>> {

    public Chart2D(List<List<Double>> values, List<String> categories, String name, boolean label) {
        super(values, categories, name, label);
    }

    @Override
    public Value clone() {
        return new Chart2D(getValues(), getCategories(), getName(), getLabel());
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Chart2D.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValues(), getCategories(), getName(), getLabel());
    }

    @Override
    public boolean equalTo(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Chart2D)) {
            return false;
        }

        Chart2D chart = (Chart2D) o;

        return getLabel() == chart.getLabel() &&
               Objects.equals(getValues(), chart.getValues()) &&
               Objects.equals(getCategories(), chart.getCategories()) &&
               Objects.equals(getName(), chart.getName());
    }
}
