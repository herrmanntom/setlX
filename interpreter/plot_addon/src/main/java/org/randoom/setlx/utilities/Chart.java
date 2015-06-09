package org.randoom.setlx.utilities;


import org.randoom.setlx.types.Value;

import java.util.List;

/**
 * Created by arne on 03.06.15.
 */
public class Chart extends Value {
    private List<Double> values;

    public List<String> getCategories() {
        return categories;
    }

    public List<Double> getValues() {
        return values;
    }

    private List<String> categories;

    public Chart(List<Double> values, List<String> categories){
        this.values = values;
        this.categories = categories;
    }
    @Override
    public Value clone() {
        return null;
    }

    @Override
    public void appendString(State state, StringBuilder sb, int tabs) {

    }

    @Override
    public int compareTo(CodeFragment other) {
        return 0;
    }

    @Override
    public long compareToOrdering() {
        return 0;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equalTo(Object other) {
        return false;
    }
}
