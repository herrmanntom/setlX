package org.randoom.setlx.plot.types;

import org.randoom.setlx.types.Value;

import java.util.List;
import java.util.Objects;

public class Chart1D extends Chart<Double> {

    public Chart1D(List<Double> values, List<String> categories, String name, boolean label) {
        super(values, categories, name, label);
    }

    @Override
    public Value clone() {
        return new Chart1D(getValues(), getCategories(), getName(), getLabel());
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Chart1D.class);

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
        if (!(o instanceof Chart1D)) {
            return false;
        }

        Chart1D chart = (Chart1D) o;

        return getLabel() == chart.getLabel() &&
               Objects.equals(getValues(), chart.getValues()) &&
               Objects.equals(getCategories(), chart.getCategories()) &&
               Objects.equals(getName(), chart.getName());
    }
}
