package org.randoom.setlx.utilities;


import org.randoom.setlx.types.Value;

import java.util.List;

/**
 * Created by arne on 03.06.15.
 */
public class Chart extends Value {
    private List values;

    public List<String> getCategories() {
        return categories;
    }

    public List getValues() {
        return values;
    }

    private List<String> categories;

    private String name;

    public String getName() {
        return name;
    }

    public Chart(List values, List<String> categories, String name){
        this.values = values;
        this.categories = categories;
        this.name = name;
    }



    @Override
    public Value clone() {
        return new Chart(this.values, this.categories, this.name);
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

        int result = (values != null ? values.hashCode() : 0);
        result = 42 * result + (categories != null ? categories.hashCode() : 0);
        result = 42 * result + (name != "" ? name.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equalTo(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chart)) return false;

        Chart c = (Chart) o;

        if(this.values != c.getValues()){return false;}
        if(this.categories != c.getCategories()){return false;}
        if(this.name != c.getName()){return false;}
        return true;
    }
}
