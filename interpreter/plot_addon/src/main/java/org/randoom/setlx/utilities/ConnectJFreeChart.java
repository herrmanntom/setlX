package org.randoom.setlx.utilities;


import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.randoom.setlx.exceptions.SetlException;

import java.util.List;

public class ConnectJFreeChart implements SetlXPlot {


    private static ConnectJFreeChart connector;

    public static ConnectJFreeChart getInstance() {
        if (connector == null) {
            connector = new ConnectJFreeChart();
        }
        return connector;
    }

    @Override
    public Canvas createCanvas() {
        DrawFrame frame = new DrawFrame("Graphic output");
        frame.setVisible(true);
        return new Canvas(frame);

    }

    @Override
    public Canvas createCanvas(String titel) {

        DrawFrame frame = new DrawFrame(titel);
        frame.setVisible(true);
        return new Canvas(frame);

    }

    @Override
    public Graph addGraph(Canvas canvas, String function, String name) throws SetlException {
        return canvas.getFrame().addDataset(name, function, false, new ChartColor(0, 0, 0));
    }

    @Override
    public Graph addGraph(Canvas canvas, String function, String name, List<Integer> color) throws SetlException {
        return canvas.getFrame().addDataset(name, function, false, new ChartColor(color.get(0), color.get(1), color.get(2)));
    }

    @Override
    public Graph addGraph(Canvas canvas, String function, String name, List<Integer> color, boolean plotArea) throws SetlException {
        return canvas.getFrame().addDataset(name, function, plotArea, new ChartColor(color.get(0), color.get(1), color.get(2)));
    }

    @Override
    public Graph addListGraph(Canvas canvas, List<List<Double>> function, String name) {
        return canvas.getFrame().addListDataset(name, function, false, new ChartColor(0, 0, 0));
    }

    @Override
    public Graph addListGraph(Canvas canvas, List<List<Double>> function, String name, List<Integer> color) {
        return canvas.getFrame().addListDataset(name, function, false, new ChartColor(color.get(0), color.get(1), color.get(2)));

    }

    @Override
    public Graph addListGraph(Canvas canvas, List<List<Double>> function, String name, List<Integer> color, boolean plotArea) {
        return canvas.getFrame().addListDataset(name, function, plotArea, new ChartColor(color.get(0), color.get(1), color.get(2)));
    }

    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name) throws SetlException {
        return canvas.getFrame().addParamDataset(name, xfunction, yfunction, false, new ChartColor(0, 0, 0));
    }

    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name, List<Integer> color) throws SetlException {
        return canvas.getFrame().addParamDataset(name, xfunction, yfunction, false, new ChartColor(color.get(0), color.get(1), color.get(2)));

    }

    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name, List<Integer> color, Boolean plotArea) throws SetlException {
        return canvas.getFrame().addParamDataset(name, xfunction, yfunction, plotArea, new ChartColor(color.get(0), color.get(1), color.get(2)));
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
    public void removeGraph(Canvas canvas, Graph graph) throws SetlException {
        canvas.getFrame().removeGraph(graph);
    }

    @Override
    public void labelAxis(Canvas canvas, String xLabel, String yLabel) {
        canvas.getFrame().setLabel(xLabel, yLabel);
    }

    @Override
    public Graph addLabel(Canvas canvas, List<Double> coordinates, String text) {
        return canvas.getFrame().addTextLabel(coordinates, text);
    }

    @Override
    public void defineTitle(Canvas canvas, String title) {
        canvas.getFrame().setTitle(title);
    }

    @Override
    public void legendVisible(Canvas canvas, Boolean visible) {
        ChartPanel chartPanel = canvas.getFrame().chartPanel;
        if (visible) {
            if (chartPanel.getChart().getLegend() == null) {
                chartPanel.getChart().addLegend(canvas.getFrame().legend);
            }
        } else {
            canvas.getFrame().legend = chartPanel.getChart().getLegend();
            chartPanel.getChart().removeLegend();
        }
    }

    @Override
    public void modScale(Canvas canvas, double xMin, double xMax, double yMin, double yMax) throws SetlException {
        canvas.getFrame().modyScale(yMin, yMax);
        canvas.getFrame().modxScale(xMin, xMax);
    }

    @Override
    public void exportCanvas(Canvas canvas, String path) {

    }

    @Override
    public void modScaleType(Canvas canvas, String xType, String yType) {

        if (xType.equalsIgnoreCase("log")) {
            canvas.getFrame().setxAxis(new LogAxis());
        } else if (xType.equalsIgnoreCase("num")) {
            canvas.getFrame().setxAxis(new NumberAxis());
        } else {
            System.out.println("Wrong x-Axis type, use log or num");
        }
        if (yType.equalsIgnoreCase("log")) {
            canvas.getFrame().setyAxis(new LogAxis());
        } else if (yType.equalsIgnoreCase("num")) {
            canvas.getFrame().setyAxis(new NumberAxis());
        } else {
            System.out.println("Wrong y-Axis type, use log or num");
        }

    }

    @Override
    public Graph addBullets(Canvas canvas, List<List<Double>> bullets){
        return canvas.getFrame().addBulletDataset("Bullets", bullets, new ChartColor(0,0,0));
    }

    @Override
    public Graph addBullets(Canvas canvas, List<List<Double>> bullets, List<Integer> color) {
        return canvas.getFrame().addBulletDataset("Bullets", bullets, new ChartColor(color.get(0), color.get(1), color.get(2)));
    }
}
