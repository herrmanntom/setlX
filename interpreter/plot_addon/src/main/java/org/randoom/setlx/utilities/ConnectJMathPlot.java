package org.randoom.setlx.utilities;


import java.util.List;

public class ConnectJMathPlot implements SetlXPlot {

    private static ConnectJMathPlot connector;

    @Override
    public Canvas createCanvas() {
        DrawFrame frame = new DrawFrame();
        return new Canvas(frame);

    }

    @Override
    public Graph addGraph(Canvas canvas, String function) {
        return null;
    }

    @Override
    public Graph addGraph(Canvas canvas, String function, String name) {
        return null;
    }

    @Override
    public Graph addGraph(Canvas canvas, String function, boolean plotArea) {
        return null;
    }

    @Override
    public Graph addGraph(Canvas canvas, String function, String name, boolean plotArea) {
        return null;
    }

    @Override
    public Graph addGraph(Canvas canvas, List function) {
        return null;
    }

    @Override
    public Graph addGraph(Canvas canvas, List function, String name) {
        return null;
    }

    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction) {
        return null;
    }

    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name) {
        return null;
    }

    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, Boolean plotArea) {
        return null;
    }

    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name, Boolean plotArea) {
        return null;
    }

    @Override
    public Graph addChart(Canvas canvas, String chartType, List values) {
        return null;
    }

    @Override
    public Graph addChart(Canvas canvas, String chartType, List values, String name) {
        return null;
    }


    @Override
    public void removeGraph(Canvas canvas, Graph graph) {

    }

    @Override
    public void insertLabel(Canvas canvas, String xLabel, String yLabel) {

    }

    @Override
    public void insertTitel(Canvas canvas, String titel) {

    }

    @Override
    public void legendVisible(Canvas canvas, Boolean visible) {

    }

    @Override
    public void modScale(Canvas canvas, List xMinMax, List yMinMax) {

    }

    @Override
    public void exportCanvas(Canvas canvas, String path) {

    }

    @Override
    public void modScaleType(Canvas canvas, String xType, String yType) {

    }


    @Override
    public void addBullet(Canvas canvas, double x, double y) {

    }

    public static ConnectJMathPlot getInstance() {
        if (connector == null) {
            connector = new ConnectJMathPlot();
        }
        return connector;
    }
}
