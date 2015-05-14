package org.randoom.setlx.utilities;

import java.util.List;

/**
 * This Interface defines how you can call your plotlibrary from setlX
 */
public interface SetlXPlot {

    /**
     * creates a canvas to draw on
     *
     * @return
     */
    public Canvas createCanvas();


    /**
     * adds a function as graph to the given canvas
     *
     * @param canvas
     * @param function
     * @return Graph object, identifier of the graph
     */
    public Graph addGraph(Canvas canvas, String function);

    /**
     * adds a function as graph to the given canvas and give it a name
     *
     * @param canvas
     * @param function
     * @param name     of the graphe
     * @return identifier of the graph
     */
    public Graph addGraph(Canvas canvas, String function, String name);

    /**
     * adds a function as graph to the given canvas and plots the area to the y-axis (integral) if plotArea ist true
     *
     * @param canvas
     * @param function
     * @param plotArea draw integral if true
     * @return
     */
    public Graph addGraph(Canvas canvas, String function, boolean plotArea);

    /**
     * adds a function as graph to the canvas, give it a name and plots the integral if plotArea is true
     *
     * @param canvas
     * @param function
     * @param name
     * @param plotArea
     * @return
     */
    public Graph addGraph(Canvas canvas, String function, String name, boolean plotArea);

    public Graph addGraph(Canvas canvas, List<List<Double>> function);

    public Graph addGraph(Canvas canvas, List<List<Double>> function, String name);

    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction);

    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name);

    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, Boolean plotArea);

    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name, Boolean plotArea);

    public Graph addChart(Canvas canvas, String chartType, List values);

    public Graph addChart(Canvas canvas, String chartType, List values, String name);

    public void removeGraph(Canvas canvas, Graph graph);

    public void insertLabel(Canvas canvas, String xLabel, String yLabel);

    public void defineTitle(Canvas canvas, String title);

    /**
     * defines if the legend describing the graphs is visible or not
     *
     * @param canvas
     * @param visible
     */
    public void legendVisible(Canvas canvas, Boolean visible);

    /**
     * sets the shown area of the graph to the given min and max values for x and y axis
     *
     * @param canvas
     * @param xMin
     * @param xMax
     * @param yMin
     * @param yMax
     */
    public void modScale(Canvas canvas, double xMin, double xMax, double yMin, double yMax);

    /**
     * exports the canvas as image file to the path
     *
     * @param canvas
     * @param path
     */
    public void exportCanvas(Canvas canvas, String path);

    /**
     * modulate the type (eq. linear or logarithmic) of the axis
     *
     * @param canvas
     * @param xType
     * @param yType
     */
    public void modScaleType(Canvas canvas, String xType, String yType);

    /**
     * add a single bullet to the canvas at the given x and y coordinates
     *
     * @param canvas
     * @param x
     * @param y
     */
    public void addBullet(Canvas canvas, double x, double y);
}
