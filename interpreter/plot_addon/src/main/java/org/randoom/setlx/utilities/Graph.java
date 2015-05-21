package org.randoom.setlx.utilities;


import org.randoom.setlx.types.Value;

import java.awt.*;
import java.util.List;

public class Graph extends Value {
    public boolean isArea() {
        return area;
    }

    private boolean area;

    public String getTitle() {
        return title;
    }

    public Graph(String title, boolean area) {
        this.title = title;
        this.area = area;
    }

    public String getXfunction() {
        return xfunction;
    }

    public List<List<Double>> getFunction() {
        return function;
    }

    public String getYfunction() {
        return yfunction;
    }

    private String title;

    private String functionstring = "";

    public String getFunctionstring() {
        return functionstring;
    }

    private Color color;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setFunctionstring(String functionstring) {
        this.functionstring = functionstring;
    }

    public void setFunction(List<List<Double>> function) {
        this.function = function;
    }

    public void setXfunction(String xfunction) {
        this.xfunction = xfunction;
    }

    public void setYfunction(String yfunction) {
        this.yfunction = yfunction;
    }

    private List<List<Double>> function = null;

    private String xfunction = "";

    private String yfunction = "";

    public boolean isBullets() {
        return bullets;
    }

    public void setBullets(boolean bullets) {
        this.bullets = bullets;
    }

    private boolean bullets;

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
