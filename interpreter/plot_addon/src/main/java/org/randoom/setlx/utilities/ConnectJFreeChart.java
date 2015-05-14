package org.randoom.setlx.utilities;


import org.jfree.data.xy.XYSeries;

import java.util.List;

public class ConnectJFreeChart implements SetlXPlot {

    private static ConnectJFreeChart connector;

    @Override
    public Canvas createCanvas() {
        DrawFrame frame = new DrawFrame();
        return new Canvas(frame);

    }

    @Override
    public Graph addGraph(Canvas canvas, String function) {
        XYSeries data = canvas.getFrame().addDataset("function", function);
        canvas.getFrame().redraw();
        return new Graph(data);
    }

    @Override
    public Graph addGraph(Canvas canvas, String function, String name) {
        XYSeries data = canvas.getFrame().addDataset(name, function);
        canvas.getFrame().redraw();
        return new Graph(data);
    }

    //TODO: at plotArea possibility
    @Override
    public Graph addGraph(Canvas canvas, String function, boolean plotArea) {
        XYSeries data = canvas.getFrame().addDataset("function", function);
        canvas.getFrame().redraw();
        return new Graph(data);
    }

    @Override
    public Graph addGraph(Canvas canvas, String function, String name, boolean plotArea) {
        XYSeries data = canvas.getFrame().addDataset(name, function);
        canvas.getFrame().redraw();
        return new Graph(data);
    }

    @Override
    public Graph addGraph(Canvas canvas, List<List<Double>> function) {
        XYSeries data = canvas.getFrame().addListDataset("function", function);
        canvas.getFrame().redraw();
        return new Graph(data);
    }

    @Override
    public Graph addGraph(Canvas canvas, List<List<Double>> function, String name) {
        XYSeries data = canvas.getFrame().addListDataset(name, function);
        canvas.getFrame().redraw();
        return new Graph(data);
    }

    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction) {
        XYSeries data = canvas.getFrame().addParamDataset("function",xfunction,yfunction);
        canvas.getFrame().redraw();
        return new Graph(data);
    }

    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name) {
        XYSeries data = canvas.getFrame().addParamDataset(name,xfunction,yfunction);
        canvas.getFrame().redraw();
        return new Graph(data);
    }

    //TODO:plotArea
    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, Boolean plotArea) {
        XYSeries data = canvas.getFrame().addParamDataset("function",xfunction,yfunction);
        canvas.getFrame().redraw();
        return new Graph(data);
    }

    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name, Boolean plotArea) {
        XYSeries data = canvas.getFrame().addParamDataset(name,xfunction,yfunction);
        canvas.getFrame().redraw();
        return new Graph(data);
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
    public void modScale(Canvas canvas, double xMin, double xMax, double yMin, double yMax) {

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

    public static ConnectJFreeChart getInstance() {
        if (connector == null) {
            connector = new ConnectJFreeChart();
        }
        return connector;
    }
}
