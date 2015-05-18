package org.randoom.setlx.utilities;


import org.jfree.data.xy.XYSeries;
import org.jfree.chart.axis.*;

import java.util.List;

public class ConnectJFreeChart implements SetlXPlot {

    private static ConnectJFreeChart connector;

    @Override
    public Canvas createCanvas() {
        DrawFrame frame = new DrawFrame();
        frame.setVisible(true);
        return new Canvas(frame);

    }

    @Override
    public Graph addGraph(Canvas canvas, String function) {
        XYSeries data = canvas.getFrame().addDataset("function", function, false);
        return new Graph(data);
    }

    @Override
    public Graph addGraph(Canvas canvas, String function, String name) {
        XYSeries data = canvas.getFrame().addDataset(name, function, false);

        return new Graph(data);
    }

    //TODO: at plotArea possibility
    @Override
    public Graph addGraph(Canvas canvas, String function, boolean plotArea) {
        XYSeries data = canvas.getFrame().addDataset("function", function, plotArea);

        return new Graph(data);
    }

    @Override
    public Graph addGraph(Canvas canvas, String function, String name, boolean plotArea) {
        XYSeries data = canvas.getFrame().addDataset(name, function, plotArea);

        return new Graph(data);
    }

    @Override
    public Graph addListGraph(Canvas canvas, List<List<Double>> function) {
        XYSeries data = canvas.getFrame().addListDataset("function", function, false);

        return new Graph(data);
    }

    @Override
    public Graph addListGraph(Canvas canvas, List<List<Double>> function, String name) {
        XYSeries data = canvas.getFrame().addListDataset(name, function, false);

        return new Graph(data);
    }

    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction) {
        XYSeries data = canvas.getFrame().addParamDataset("function", xfunction, yfunction, false);

        return new Graph(data);
    }

    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name) {
        XYSeries data = canvas.getFrame().addParamDataset(name,xfunction,yfunction, false);

        return new Graph(data);
    }

    //TODO:plotArea
    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, Boolean plotArea) {
        XYSeries data = canvas.getFrame().addParamDataset("function",xfunction,yfunction, plotArea);

        return new Graph(data);
    }

    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name, Boolean plotArea) {
        XYSeries data = canvas.getFrame().addParamDataset(name,xfunction,yfunction, plotArea);

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
    public void defineTitle(Canvas canvas, String title) {

    }

    @Override
    public void legendVisible(Canvas canvas, Boolean visible) {

    }

    @Override
    public void modScale(Canvas canvas, double xMin, double xMax, double yMin, double yMax) {
        canvas.getFrame().modyScale(yMax, yMin);
        canvas.getFrame().modxScale(xMax, xMin);
    }

    @Override
    public void exportCanvas(Canvas canvas, String path) {

    }

    @Override
    public void modScaleType(Canvas canvas, String xType, String yType) {
        System.out.println(xType+" "+yType);
        if(xType.equalsIgnoreCase("\"log\"")){
            canvas.getFrame().setxAxis(new LogAxis());
        }
        else if(xType.equalsIgnoreCase("\"num\"")){
            canvas.getFrame().setxAxis(new NumberAxis());
        }
        else{
            System.out.println("Wrong x-Axis type, use log or num");
        }
        if(yType.equalsIgnoreCase("\"log\"")){
            canvas.getFrame().setyAxis(new LogAxis());
        }
        else if(yType.equalsIgnoreCase("\"num\"")){
            canvas.getFrame().setyAxis(new NumberAxis());
        }
        else{
            System.out.println("Wrong y-Axis type, use log or num");
        }

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
