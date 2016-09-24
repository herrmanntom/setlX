package org.randoom.setlx.plot.utilities;


import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.randoom.setlx.exceptions.FileNotWritableException;
import org.randoom.setlx.exceptions.IllegalRedefinitionException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.plot.types.*;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.types.Value;

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
    public org.randoom.setlx.plot.types.Canvas createCanvas() {
        org.randoom.setlx.plot.types.Canvas canvas = new org.randoom.setlx.plot.types.Canvas(new FrameWrapper());
        canvas.setTitle("Graphic output");
        return canvas;
    }

    @Override
    public org.randoom.setlx.plot.types.Canvas createCanvas(String title) {
        org.randoom.setlx.plot.types.Canvas canvas = new org.randoom.setlx.plot.types.Canvas(new FrameWrapper());
        canvas.setTitle(title);
        return canvas;
    }


    @Override
    public Graph addGraph(org.randoom.setlx.plot.types.Canvas canvas, String function, String name, State interpreterState, List<Integer> color, boolean plotArea) throws SetlException {
        if(canvas.getFrame().getFrameType() == FrameWrapper.VIRGIN_FRAME){
            canvas.getFrame().setFrameType(FrameWrapper.DRAW_FRAME);
            canvas.getFrame().setFrame(new DrawFrame(canvas.getTitle(), canvas.getFrame().getWidth(), canvas.getFrame().getHeight()));
        }
        else if(canvas.getFrame().getFrameType() >= FrameWrapper.BAR_FRAME){
            throw new IllegalRedefinitionException("This Canvas can only be used for Graphs, not for Charts. Create a new Canvas, to draw Graphs");

        }
        return ((DrawFrame) canvas.getFrame().getFrame()).addDataset(name, function, interpreterState, plotArea, new ChartColor(color.get(0), color.get(1), color.get(2)));
    }


    @Override
    public Graph addListGraph(org.randoom.setlx.plot.types.Canvas canvas, List<List<Double>> function, String name, List<Integer> color, boolean plotArea) throws IllegalRedefinitionException {
        if(canvas.getFrame().getFrameType() == FrameWrapper.VIRGIN_FRAME){
            canvas.getFrame().setFrameType(FrameWrapper.DRAW_FRAME);
            canvas.getFrame().setFrame(new DrawFrame(canvas.getTitle(), canvas.getFrame().getWidth(), canvas.getFrame().getHeight()));
        }
        else if(canvas.getFrame().getFrameType() >= FrameWrapper.BAR_FRAME){
            throw new IllegalRedefinitionException("This Canvas can only be used for Graphs, not for Charts. Create a new Canvas, to draw Graphs");

        }
        return ((DrawFrame)canvas.getFrame().getFrame()).addListDataset(name, function, plotArea, new ChartColor(color.get(0), color.get(1), color.get(2)));
    }


    @Override
    public Graph addParamGraph(org.randoom.setlx.plot.types.Canvas canvas, String xfunction, String yfunction, String name, State interpreterState, List<Integer> color, Boolean plotArea, List<Double> limits) throws SetlException {
        if(canvas.getFrame().getFrameType() == FrameWrapper.VIRGIN_FRAME){
            canvas.getFrame().setFrameType(FrameWrapper.DRAW_FRAME);
            canvas.getFrame().setFrame(new DrawFrame(canvas.getTitle(), canvas.getFrame().getWidth(), canvas.getFrame().getHeight()));
        }
        else if(canvas.getFrame().getFrameType() >= FrameWrapper.BAR_FRAME){
            throw new IllegalRedefinitionException("This Canvas can only be used for Graphs, not for Charts. Create a new Canvas, to draw Graphs");

        }
        return ((DrawFrame)canvas.getFrame().getFrame()).addParamDataset(name, xfunction, yfunction, interpreterState, plotArea, new ChartColor(color.get(0), color.get(1), color.get(2)), limits);
    }

    @Override
    public Chart1D addBarChart(org.randoom.setlx.plot.types.Canvas canvas, List<Double> values, List<String> categories) throws IllegalRedefinitionException {
        if(canvas.getFrame().getFrameType() == FrameWrapper.DRAW_FRAME){
            throw new IllegalRedefinitionException("This canvas is defined for graphs. Create a new canvas to draw charts");
        }
        if(canvas.getFrame().getFrameType() == FrameWrapper.BOX_FRAME || canvas.getFrame().getFrameType() == FrameWrapper.PIE_FRAME){
            throw new IllegalRedefinitionException("This canvas is defined for BarCharts. Create a new canvas to draw another type of charts");
        }
        if(canvas.getFrame().getFrameType() == FrameWrapper.VIRGIN_FRAME){
            canvas.getFrame().setFrame(new BarFrame(canvas.getTitle(), canvas.getFrame().getWidth(), canvas.getFrame().getHeight()));
            canvas.getFrame().setFrameType(FrameWrapper.BAR_FRAME);
        }
        return ((BarFrame)canvas.getFrame().getFrame()).addBarChart(values, categories, "series"+canvas.getFrame().getFrame().getChartCount());

    }

    @Override
    public Chart1D addBarChart(org.randoom.setlx.plot.types.Canvas canvas, List<Double> values, List<String> categories, String name) throws IllegalRedefinitionException {
        if(canvas.getFrame().getFrameType() == FrameWrapper.DRAW_FRAME){
            throw new IllegalRedefinitionException("This canvas is defined for graphs. Create a new canvas to draw charts");
        }
        if(canvas.getFrame().getFrameType() == FrameWrapper.BOX_FRAME || canvas.getFrame().getFrameType() == FrameWrapper.PIE_FRAME){
            throw new IllegalRedefinitionException("This canvas is defined for BarCharts. Create a new canvas to draw another type of charts");

        }
        if(canvas.getFrame().getFrameType() == FrameWrapper.VIRGIN_FRAME){
            canvas.getFrame().setFrame(new BarFrame(canvas.getTitle(), canvas.getFrame().getWidth(), canvas.getFrame().getHeight()));
            canvas.getFrame().setFrameType(FrameWrapper.BAR_FRAME);
        }
        return ((BarFrame)canvas.getFrame().getFrame()).addBarChart(values, categories, name);

    }

    @Override
    public Chart1D addPieChart(org.randoom.setlx.plot.types.Canvas canvas, List<Double> values, List<String> categories) throws IllegalRedefinitionException {
        if(canvas.getFrame().getFrameType() == FrameWrapper.DRAW_FRAME){
            throw new IllegalRedefinitionException("This canvas is defined for graphs. Create a new canvas to draw charts");
        }
        if(canvas.getFrame().getFrameType() == FrameWrapper.BOX_FRAME || canvas.getFrame().getFrameType() == FrameWrapper.BAR_FRAME){
            throw new IllegalRedefinitionException("This canvas is defined for PieCharts. Create a new canvas to draw another type of charts");

        }
        if(canvas.getFrame().getFrameType() == FrameWrapper.VIRGIN_FRAME){
            canvas.getFrame().setFrame(new PieFrame(canvas.getTitle(), canvas.getFrame().getWidth(), canvas.getFrame().getHeight()));
            canvas.getFrame().setFrameType(FrameWrapper.PIE_FRAME);
        }
        return ((PieFrame)canvas.getFrame().getFrame()).addPieChart(values, categories);

    }

    @Override
    public Chart2D addBoxChart(org.randoom.setlx.plot.types.Canvas canvas, List<List<Double>> values, List<String> categories) throws IllegalRedefinitionException {
        if(canvas.getFrame().getFrameType() == FrameWrapper.DRAW_FRAME){
            throw new IllegalRedefinitionException("This canvas is defined for graphs. Create a new canvas to draw charts");
        }
        if(canvas.getFrame().getFrameType() == FrameWrapper.BAR_FRAME || canvas.getFrame().getFrameType() == FrameWrapper.PIE_FRAME){
            throw new IllegalRedefinitionException("This canvas is defined for BoxCharts. Create a new canvas to draw another type of charts");

        }
        if(canvas.getFrame().getFrameType() == FrameWrapper.VIRGIN_FRAME){
            canvas.getFrame().setFrame(new BoxFrame(canvas.getTitle(), canvas.getFrame().getWidth(), canvas.getFrame().getHeight()));
            canvas.getFrame().setFrameType(FrameWrapper.BOX_FRAME);
        }
        return ((BoxFrame)canvas.getFrame().getFrame()).addBoxChart(values, categories,  "series"+canvas.getFrame().getFrame().getChartCount() );

    }

    @Override
    public Chart2D addBoxChart(org.randoom.setlx.plot.types.Canvas canvas, List<List<Double>> values, List<String> categories, String name) throws IllegalRedefinitionException {
        if(canvas.getFrame().getFrameType() == FrameWrapper.DRAW_FRAME){
            throw new IllegalRedefinitionException("This canvas is defined for graphs. Create a new canvas to draw charts");
        }
        if(canvas.getFrame().getFrameType() == FrameWrapper.BAR_FRAME || canvas.getFrame().getFrameType() == FrameWrapper.PIE_FRAME){
            throw new IllegalRedefinitionException("This canvas is defined for BoxCharts. Create a new canvas to draw another type of charts");

        }
        if(canvas.getFrame().getFrameType() == FrameWrapper.VIRGIN_FRAME){
            canvas.getFrame().setFrame(new BoxFrame(canvas.getTitle(), canvas.getFrame().getWidth(), canvas.getFrame().getHeight()));
            canvas.getFrame().setFrameType(FrameWrapper.BOX_FRAME);
        }
        return ((BoxFrame)canvas.getFrame().getFrame()).addBoxChart(values, categories, name );

    }

    @Override
    public void removeGraph(org.randoom.setlx.plot.types.Canvas canvas, Value value) throws SetlException {

        canvas.getFrame().getFrame().removeGraph(value);
    }

    @Override
    public void labelAxis(org.randoom.setlx.plot.types.Canvas canvas, String xLabel, String yLabel) throws IllegalRedefinitionException {
        canvas.getFrame().getFrame().setLabel(xLabel, yLabel);

    }

    @Override
    public Value addLabel(org.randoom.setlx.plot.types.Canvas canvas, List<Double> coordinates, String text) throws UndefinedOperationException {
        if(canvas.getFrame().getFrameType() == FrameWrapper.VIRGIN_FRAME){
            throw new UndefinedOperationException("label cannot be added, if no Graph or Chart is defined. Please add a Graph or Chart first");
        }
        return canvas.getFrame().getFrame().addTextLabel(coordinates, text);
    }

    @Override
    public void defineTitle(org.randoom.setlx.plot.types.Canvas canvas, String title) {
        canvas.setTitle(title);
        if(canvas.getFrame().getFrame() != null) {
            canvas.getFrame().getFrame().setTitle(title);
        }
    }

    @Override
    public void legendVisible(org.randoom.setlx.plot.types.Canvas canvas, Boolean visible) {
        ChartPanel chartPanel = canvas.getFrame().getFrame().chartPanel;
        canvas.getFrame().getFrame().setLegendVisible(visible);
        if (visible) {
            if (chartPanel.getChart().getLegend() == null) {
                chartPanel.getChart().addLegend(canvas.getFrame().getFrame().legend);
            }
        } else {
            canvas.getFrame().getFrame().legend = chartPanel.getChart().getLegend();
            chartPanel.getChart().removeLegend();
        }

    }

    @Override
    public void modScale(org.randoom.setlx.plot.types.Canvas canvas, double xMin, double xMax, double yMin, double yMax) throws SetlException {
        if(canvas.getFrame().getFrameType() >= FrameWrapper.BAR_FRAME){
            throw new IllegalRedefinitionException("This Canvas can only be used for Graphs, not for Charts. Create a new Canvas, to draw Graphs");
        }
        ((DrawFrame)canvas.getFrame().getFrame()).modyScale(yMin, yMax);
        ((DrawFrame)canvas.getFrame().getFrame()).modxScale(xMin, xMax);
    }

    @Override
    public void exportCanvas(org.randoom.setlx.plot.types.Canvas canvas, String path) throws FileNotWritableException {
        BufferedImage image = new BufferedImage(canvas.getFrame().getFrame().getWidth(), canvas.getFrame().getFrame().getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        canvas.getFrame().getFrame().paint(graphics2D);
        try {
            ImageIO.write(image, "png", new File(path + ".png"));
        } catch (IOException except) {
            throw new FileNotWritableException("write couldnt be completed", except);
        }

    }

    @Override
    public void modScaleType(org.randoom.setlx.plot.types.Canvas canvas, String xType, String yType) throws UndefinedOperationException, IllegalRedefinitionException {

        if(canvas.getFrame().getFrameType() >= FrameWrapper.BAR_FRAME){
            throw new IllegalRedefinitionException("This Canvas can only be used for Graphs, not for Charts. Create a new Canvas, to draw Graphs");
        }
        LogarithmicAxis log = new LogarithmicAxis(canvas.getFrame().getFrame().getyAxis().getLabel());
        log.setAllowNegativesFlag(true);
        if (xType.equalsIgnoreCase("log")) {
            ((DrawFrame)canvas.getFrame().getFrame()).setxAxis(log);
        } else if (xType.equalsIgnoreCase("num")) {
            ((DrawFrame)canvas.getFrame().getFrame()).setxAxis(new NumberAxis(canvas.getFrame().getFrame().getxAxis().getLabel()));
        } else {
            throw new UndefinedOperationException("Axis type for x-Axis can either be log or num");
        }
        if (yType.equalsIgnoreCase("log")) {
            ((DrawFrame)canvas.getFrame().getFrame()).setyAxis(log);
        } else if (yType.equalsIgnoreCase("num")) {
            ((DrawFrame)canvas.getFrame().getFrame()).setyAxis(new NumberAxis(canvas.getFrame().getFrame().getyAxis().getLabel()));
        } else {
            throw new UndefinedOperationException("Axis type for y-Axis can either be log or num");
        }

    }


    @Override
    public Graph addBullets(org.randoom.setlx.plot.types.Canvas canvas, List<List<Double>> bullets, List<Integer> color, Double bulletSize) throws IllegalRedefinitionException {
        if(canvas.getFrame().getFrameType() == FrameWrapper.VIRGIN_FRAME){
            canvas.getFrame().setFrameType(FrameWrapper.DRAW_FRAME);
            canvas.getFrame().setFrame(new DrawFrame(canvas.getTitle(), canvas.getFrame().getWidth(), canvas.getFrame().getHeight()));
        }
        else if(canvas.getFrame().getFrameType() >= FrameWrapper.BAR_FRAME){
            throw new IllegalRedefinitionException("This Canvas can only be used for Graphs, not for Charts. Create a new Canvas, to draw Graphs");
        }
        return ((DrawFrame)canvas.getFrame().getFrame()).addBulletDataset("Bullets", bullets, new ChartColor(color.get(0), color.get(1), color.get(2)), bulletSize);
    }

    @Override
    public void modSize(org.randoom.setlx.plot.types.Canvas canvas, List<Double> size) {
        canvas.getFrame().setWidth(size.get(0));
        canvas.getFrame().setHeight(size.get(1));
        if(!(canvas.getFrame().getFrameType() == FrameWrapper.VIRGIN_FRAME)){
            canvas.getFrame().getFrame().modSize(size.get(0), size.get(1));
        }

    }
}
