package org.randoom.setlx.plot.types;


import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.types.Value;

import java.awt.*;
import java.util.List;

public class Graph extends Value {
    private boolean area;
    private String title;
    private String functionstring = "";
    private Color color;
    private List<List<Double>> function = null;
    private String xfunction = "";
    private String yfunction = "";
    private boolean bullets;
    private State interpreterState;

    public Double getBulletSize() {
        return bulletSize;
    }

    public void setBulletSize(Double bulletSize) {
        this.bulletSize = bulletSize;
    }

    private Double bulletSize;

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    private List<Double> coordinates = null;


    public Graph(String title, boolean area, State pState) {
        this.title = title;
        this.area = area;
        this.interpreterState = pState;
    }


    public boolean isArea() {
        return area;
    }

    public String getTitle() {
        return title;
    }

    public State getInterpreterState(){
        return this.interpreterState;
    }

    public String getXfunction() {
        return xfunction;
    }

    public void setXfunction(String xfunction) {
        this.xfunction = xfunction;
    }

    public List<List<Double>> getFunction() {
        return function;
    }

    public void setFunction(List<List<Double>> function) {
        this.function = function;
    }

    public String getYfunction() {
        return yfunction;
    }

    public void setYfunction(String yfunction) {
        this.yfunction = yfunction;
    }

    public String getFunctionstring() {
        return functionstring;
    }

    public void setFunctionstring(String functionstring) {
        this.functionstring = functionstring;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isBullets() {
        return bullets;
    }

    public void setBullets(boolean bullets) {
        this.bullets = bullets;
    }


    @Override
    public Value clone() {
        Graph clone = new Graph(this.title, this.area, this.interpreterState);
        clone.setFunction(this.function);
        clone.setFunctionstring(this.functionstring);
        clone.setColor(this.color);
        clone.setXfunction(this.xfunction);
        clone.setYfunction(this.yfunction);
        clone.setBullets(this.bullets);
        clone.setBulletSize(this.bulletSize);

        return clone;
    }

    @Override
    public void appendString(State state, StringBuilder sb, int tabs) {

    }

    @Override
    public int compareTo(CodeFragment other) {
        if (other.getClass() == Graph.class) {
            return 0;
        }  else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(Graph.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equalTo(Object o) {
        if (this == o) return true;
        if (!(o instanceof Graph)) return false;

        Graph graph = (Graph) o;

        if (area != graph.area) return false;
        if (bullets != graph.bullets) return false;
        if (title != null ? !title.equals(graph.title) : graph.title != null) return false;
        if (functionstring != null ? !functionstring.equals(graph.functionstring) : graph.functionstring != null)
            return false;
        if (color != null ? !color.equals(graph.color) : graph.color != null) return false;
        if (function != null ? !function.equals(graph.function) : graph.function != null) return false;
        if (xfunction != null ? !xfunction.equals(graph.xfunction) : graph.xfunction != null) return false;
        if (bulletSize != null ? !(bulletSize == graph.bulletSize) : graph.bulletSize != null) return false;

        return !(yfunction != null ? !yfunction.equals(graph.yfunction) : graph.yfunction != null);

    }

    @Override
    public int hashCode() {
        int result = (area ? 1 : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (functionstring != null ? functionstring.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (function != null ? function.hashCode() : 0);
        result = 31 * result + (xfunction != null ? xfunction.hashCode() : 0);
        result = 31 * result + (yfunction != null ? yfunction.hashCode() : 0);
        result = 31 * result + (bullets ? 1 : 0);
        result = 31 * result + bulletSize.intValue();
        return result;
    }
}
