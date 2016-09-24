package org.randoom.setlx.plot.types;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 * Created by arne on 03.06.15.
 */
public abstract class Chart<T> extends Value {
    private List<T> values;

    public List<String> getCategories() {
        return categories;
    }

    public List<T> getValues() {
        return values;
    }

    private List<String> categories;

    private String name;

    public String getName() {
        return name;
    }

    private boolean label;

    public boolean getLabel() {
        return label;
    }

    protected Chart(List<T> values, List<String> categories, String name, boolean label){
        this.values = values;
        this.categories = categories;
        this.name = name;
        this.label = label;
    }

    @Override
    public void appendString(State state, StringBuilder sb, int tabs) {

    }

    @Override
    public int compareTo(CodeFragment other) {
        if (this.getClass() == other.getClass()) {
            return 0;
        }  else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }
}
