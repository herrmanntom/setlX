package org.randoom.setlx.utilities;


import org.jfree.data.xy.XYSeries;
import org.randoom.setlx.types.Value;

public class Graph extends Value {


    private XYSeries series;
    public Graph(XYSeries series) {
        this.series = series;
    }

    public XYSeries getSeries() {
        return series;
    }

    @Override
    public Value clone() {
        return new Graph(this.series);
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
