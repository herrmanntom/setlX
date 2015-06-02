package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.FileNotWritableException;
import org.randoom.setlx.exceptions.SetlException;

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


    public Canvas createCanvas(String titel);

    /**
     * adds a function as graph to the given canvas and give it a name
     *
     * @param canvas
     * @param function
     * @param name     of the graphe
     * @param interpreterState
     * @return identifier of the graph
     */
    public Graph addGraph(Canvas canvas, String function, String name, State interpreterState) throws SetlException;

    /**
     * adds a function as graph to the canvas, give it a name and plots the integral if plotArea is true
     *
     * @param canvas
     * @param function
     * @param name
     * @param interpreterState
     *@param color  @return
     */
    public Graph addGraph(Canvas canvas, String function, String name, State interpreterState, List<Integer> color) throws SetlException;

    /**
     * adds a function as graph to the given canvas and plots the area to the y-axis (integral) if plotArea ist true
     *
     * @param canvas
     * @param function
     * @param interpreterState
     *@param color
     * @param plotArea draw integral if true   @return
     */
    public Graph addGraph(Canvas canvas, String function, String name, State interpreterState, List<Integer> color, boolean plotArea) throws SetlException;

    /**
     * adds a graph consisting of single points
     *
     * @param canvas
     * @param function list of points to be plotted
     * @return
     */
    public Graph addListGraph(Canvas canvas, List<List<Double>> function, String name);

    /**
     * adds a graph consisting of single points and a name for the graph
     *
     * @param canvas
     * @param function list of points to be plotted
     * @param name
     * @param color
     * @return
     */
    public Graph addListGraph(Canvas canvas, List<List<Double>> function, String name, List<Integer> color);

    public Graph addListGraph(Canvas canvas, List<List<Double>> function, String name, List<Integer> color, boolean plotArea);

    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name, State interpreterState, List<Double> limits) throws SetlException;

    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name, State interpreterState, List<Integer> color, List<Double> limits) throws SetlException;

    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name, State interpreterState, List<Integer> color, Boolean plotArea, List<Double> limits) throws SetlException;

    public Graph addChart(Canvas canvas, String chartType, List values);

    public Graph addChart(Canvas canvas, String chartType, List values, String name);

    public void removeGraph(Canvas canvas, Graph graph) throws SetlException;

    public void labelAxis(Canvas canvas, String xLabel, String yLabel);

    public Graph addLabel(Canvas canvas, List<Double> coordinates, String text);

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
    public void modScale(Canvas canvas, double xMin, double xMax, double yMin, double yMax) throws SetlException;

    /**
     * exports the canvas as image file to the path
     *
     * @param canvas
     * @param path
     */
    public void exportCanvas(Canvas canvas, String path) throws FileNotWritableException;

    /**
     * modulate the type (eq. linear or logarithmic) of the axis
     *
     * @param canvas
     * @param xType
     * @param yType
     */
    public void modScaleType(Canvas canvas, String xType, String yType);

    public Graph addBullets(Canvas canvas, List<List<Double>> bullets);

    /**
     * add a single bullet to the canvas at the given x and y coordinates
     *
     * @param canvas
     * @param bullets
     * @param color
     */
    public Graph addBullets(Canvas canvas, List<List<Double>> bullets, List<Integer> color);

}
