package org.randoom.setlx.utilities;


import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.randoom.setlx.exceptions.FileNotWritableException;
import org.randoom.setlx.exceptions.SetlException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name, List<Double> limits) throws SetlException {
        return canvas.getFrame().addParamDataset(name, xfunction, yfunction, false, new ChartColor(0, 0, 0), limits);
    }

    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name, List<Integer> color, List<Double> limits) throws SetlException {
        return canvas.getFrame().addParamDataset(name, xfunction, yfunction, false, new ChartColor(color.get(0), color.get(1), color.get(2)), limits);

    }

    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name, List<Integer> color, Boolean plotArea, List<Double> limits) throws SetlException {
        return canvas.getFrame().addParamDataset(name, xfunction, yfunction, plotArea, new ChartColor(color.get(0), color.get(1), color.get(2)), limits);
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
    public void exportCanvas(Canvas canvas, String path) throws FileNotWritableException {
        System.out.println("test");
        BufferedImage image = new BufferedImage(canvas.getFrame().getWidth(), canvas.getFrame().getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D= image.createGraphics();
        canvas.getFrame().paint(graphics2D);
        try {
            ImageIO.write(image, "png", new File(path+".png"));
        }
        catch (IOException except){
            throw new FileNotWritableException("write couldnt be completed", except);
        }

    }

    @Override
    public void modScaleType(Canvas canvas, String xType, String yType) {

        if (xType.equalsIgnoreCase("log")) {
            canvas.getFrame().setxAxis(new LogarithmicAxis(canvas.getFrame().getxAxis().getLabel()));
        } else if (xType.equalsIgnoreCase("num")) {
            canvas.getFrame().setxAxis(new NumberAxis(canvas.getFrame().getxAxis().getLabel()));
        } else {
            System.out.println("Wrong x-Axis type, use log or num");
        }
        if (yType.equalsIgnoreCase("log")) {
            canvas.getFrame().setyAxis(new LogarithmicAxis(canvas.getFrame().getyAxis().getLabel()));
        } else if (yType.equalsIgnoreCase("num")) {
            canvas.getFrame().setyAxis(new NumberAxis(canvas.getFrame().getyAxis().getLabel()));
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
