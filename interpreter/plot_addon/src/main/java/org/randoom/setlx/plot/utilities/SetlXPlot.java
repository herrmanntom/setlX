package org.randoom.setlx.plot.utilities;

import org.randoom.setlx.exceptions.FileNotWritableException;
import org.randoom.setlx.exceptions.IllegalRedefinitionException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.types.Value;

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
     * adds a function as graph to the given canvas and plots the area to the y-axis (integral) if plotArea ist true
     ** @param canvas
     * @param function
     * @param name             of the graphe
     * @param interpreterState
     * @param color
     * @param plotArea         draw integral if true
     * @return identifier of the graph
     */
    public Graph addGraph(Canvas canvas, String function, String name, State interpreterState, List<Integer> color, boolean plotArea) throws SetlException;

    /**
     * adds a graph consisting of single points
     *
     * @param canvas
     * @param function list of points to be plotted
     * @return
     */

    public Graph addListGraph(Canvas canvas, List<List<Double>> function, String name, List<Integer> color, boolean plotArea) throws IllegalRedefinitionException;


    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name, State interpreterState, List<Integer> color, Boolean plotArea, List<Double> limits) throws SetlException;


    public Chart addBarChart(Canvas canvas, List<Double> values, List<String> categories) throws IllegalRedefinitionException;
    public Chart addBarChart(Canvas canvas, List<Double> values, List<String> categories, String name) throws IllegalRedefinitionException;

    public Chart addPieChart(Canvas canvas, List<Double> values, List<String> categories) throws IllegalRedefinitionException;

    public Chart addBoxChart(Canvas canvas, List<List<Double>> values, List<String> categories) throws IllegalRedefinitionException;
    public Chart addBoxChart(Canvas canvas, List<List<Double>> values, List<String> categories, String name) throws IllegalRedefinitionException;

    public void removeGraph(Canvas canvas, Value value) throws SetlException;

    public void labelAxis(Canvas canvas, String xLabel, String yLabel) throws IllegalRedefinitionException;

    public Value addLabel(Canvas canvas, List<Double> coordinates, String text) throws UndefinedOperationException;

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
    public void modScaleType(Canvas canvas, String xType, String yType) throws UndefinedOperationException, IllegalRedefinitionException;

    /**
     * add a single bullet to the canvas at the given x and y coordinates
     *
     * @param canvas
     * @param bullets
     * @param color
     * @param bulletSize
     */
    public Graph addBullets(Canvas canvas, List<List<Double>> bullets, List<Integer> color, Double bulletSize) throws IllegalRedefinitionException;


    public void modSize(Canvas canvas, List<Double> size);
}
