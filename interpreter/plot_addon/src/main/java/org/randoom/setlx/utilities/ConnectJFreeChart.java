package org.randoom.setlx.utilities;


import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.randoom.setlx.exceptions.FileNotWritableException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;

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
        Canvas canvas = new Canvas(null);
        canvas.setTitle("Graphic output");
        return canvas;
    }

    @Override
    public Canvas createCanvas(String title) {
        Canvas canvas = new Canvas(null);
        canvas.setTitle(title);
        return canvas;
    }

    @Override
    public Graph addGraph(Canvas canvas, String function, String name, State interpreterState) throws SetlException {
        if(canvas.getFrameType() == Canvas.VIRGIN_FRAME){
            canvas.setFrameType(Canvas.DRAW_FRAME);
            canvas.setFrame(new DrawFrame(canvas.getTitle()));
        }
        else if(canvas.getFrameType() >= Canvas.BAR_FRAME){
            System.out.println("Canvas is used for Diagrams. Not possible to insert Graph");
            return null;
        }
        return ((DrawFrame) canvas.getFrame()).addDataset(name, function, interpreterState, false, new ChartColor(0, 0, 0));
    }

    @Override
    public Graph addGraph(Canvas canvas, String function, String name, State interpreterState, List<Integer> color) throws SetlException {
        if(canvas.getFrameType() == Canvas.VIRGIN_FRAME){
            canvas.setFrameType(Canvas.DRAW_FRAME);
            canvas.setFrame(new DrawFrame(canvas.getTitle()));
        }
        else if(canvas.getFrameType() >= Canvas.BAR_FRAME){
            System.out.println("Canvas is used for Diagramms. Not possible to insert Graph");
            return null;
        }
        return ((DrawFrame) canvas.getFrame()).addDataset(name, function, interpreterState, false, new ChartColor(color.get(0), color.get(1), color.get(2)));
    }

    @Override
    public Graph addGraph(Canvas canvas, String function, String name, State interpreterState, List<Integer> color, boolean plotArea) throws SetlException {
        if(canvas.getFrameType() == Canvas.VIRGIN_FRAME){
            canvas.setFrameType(Canvas.DRAW_FRAME);
            canvas.setFrame(new DrawFrame(canvas.getTitle()));
        }
        else if(canvas.getFrameType() >= Canvas.BAR_FRAME){
            System.out.println("Canvas is used for Diagramms. Not possible to insert Graph");
            return null;
        }
        return ((DrawFrame) canvas.getFrame()).addDataset(name, function, interpreterState, plotArea, new ChartColor(color.get(0), color.get(1), color.get(2)));
    }

    @Override
    public Graph addListGraph(Canvas canvas, List<List<Double>> function, String name) {
        if(canvas.getFrameType() == Canvas.VIRGIN_FRAME){
            canvas.setFrameType(Canvas.DRAW_FRAME);
            canvas.setFrame(new DrawFrame(canvas.getTitle()));
        }
        else if(canvas.getFrameType() >= Canvas.BAR_FRAME){
            System.out.println("Canvas is used for Diagramms. Not possible to insert Graph");
            return null;
        }
        return ((DrawFrame)canvas.getFrame()).addListDataset(name, function, false, new ChartColor(0, 0, 0));
    }

    @Override
    public Graph addListGraph(Canvas canvas, List<List<Double>> function, String name, List<Integer> color) {
        if(canvas.getFrameType() == Canvas.VIRGIN_FRAME){
            canvas.setFrameType(Canvas.DRAW_FRAME);
            canvas.setFrame(new DrawFrame(canvas.getTitle()));
        }
        else if(canvas.getFrameType() >= Canvas.BAR_FRAME){
            System.out.println("Canvas is used for Diagramms. Not possible to insert Graph");
            return null;
        }
        return ((DrawFrame)canvas.getFrame()).addListDataset(name, function, false, new ChartColor(color.get(0), color.get(1), color.get(2)));

    }

    @Override
    public Graph addListGraph(Canvas canvas, List<List<Double>> function, String name, List<Integer> color, boolean plotArea) {
        if(canvas.getFrameType() == Canvas.VIRGIN_FRAME){
            canvas.setFrameType(Canvas.DRAW_FRAME);
            canvas.setFrame(new DrawFrame(canvas.getTitle()));
        }
        else if(canvas.getFrameType() >= Canvas.BAR_FRAME){
            System.out.println("Canvas is used for Diagramms. Not possible to insert Graph");
            return null;
        }
        return ((DrawFrame)canvas.getFrame()).addListDataset(name, function, plotArea, new ChartColor(color.get(0), color.get(1), color.get(2)));
    }

    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name, State interpreterState, List<Double> limits) throws SetlException {
        if(canvas.getFrameType() == Canvas.VIRGIN_FRAME){
            canvas.setFrameType(Canvas.DRAW_FRAME);
            canvas.setFrame(new DrawFrame(canvas.getTitle()));
        }
        else if(canvas.getFrameType() >= Canvas.BAR_FRAME){
            System.out.println("Canvas is used for Diagramms. Not possible to insert Graph");
            return null;
        }
        return ((DrawFrame)canvas.getFrame()).addParamDataset(name, xfunction, yfunction, interpreterState, false, new ChartColor(0, 0, 0), limits);
    }

    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name, State interpreterState, List<Integer> color, List<Double> limits) throws SetlException {
        if(canvas.getFrameType() == Canvas.VIRGIN_FRAME){
            canvas.setFrameType(Canvas.DRAW_FRAME);
            canvas.setFrame(new DrawFrame(canvas.getTitle()));
        }
        else if(canvas.getFrameType() >= Canvas.BAR_FRAME){
            System.out.println("Canvas is used for Diagramms. Not possible to insert Graph");
            return null;
        }
        return ((DrawFrame)canvas.getFrame()).addParamDataset(name, xfunction, yfunction, interpreterState, false, new ChartColor(color.get(0), color.get(1), color.get(2)), limits);

    }

    @Override
    public Graph addParamGraph(Canvas canvas, String xfunction, String yfunction, String name, State interpreterState, List<Integer> color, Boolean plotArea, List<Double> limits) throws SetlException {
        if(canvas.getFrameType() == Canvas.VIRGIN_FRAME){
            canvas.setFrameType(Canvas.DRAW_FRAME);
            canvas.setFrame(new DrawFrame(canvas.getTitle()));
        }
        else if(canvas.getFrameType() >= Canvas.BAR_FRAME){
            System.out.println("Canvas is used for Diagramms. Not possible to insert Graph");
            return null;
        }
        return ((DrawFrame)canvas.getFrame()).addParamDataset(name, xfunction, yfunction, interpreterState, plotArea, new ChartColor(color.get(0), color.get(1), color.get(2)), limits);
    }

    @Override
    public Chart addChart(Canvas canvas, String chartType, List<Double> values, List<String> categories) throws UndefinedOperationException {
        if(canvas.getFrameType() == Canvas.DRAW_FRAME){
            System.out.println("Canvas is used for Graphs. Not possible to insert Diagram.");
            return null;
        }
        Chart returnChart = null;
        if(chartType.equalsIgnoreCase("bar")){
            if(canvas.getFrameType() == Canvas.BOX_FRAME || canvas.getFrameType() == Canvas.PIE_FRAME){
                System.out.println("Wrong Diagram type. This Canvas is for BarCharts");
                return null;
            }
            if(canvas.getFrameType() == Canvas.VIRGIN_FRAME){
                canvas.setFrame(new BarFrame(canvas.getTitle()));
                canvas.setFrameType(Canvas.BAR_FRAME);
                returnChart = ((BarFrame)canvas.getFrame()).addBarChart(values, categories);
            }
        }else if(chartType.equalsIgnoreCase("pie")){
            if(canvas.getFrameType() == Canvas.BOX_FRAME || canvas.getFrameType() == Canvas.BAR_FRAME){
                System.out.println("Wrong Diagram type. This Canvas is for PieCharts");
                return null;
            }
            if(canvas.getFrameType() == Canvas.VIRGIN_FRAME){
                canvas.setFrame(new PieFrame(canvas.getTitle()));
            }
            returnChart = ((PieFrame)canvas.getFrame()).addPieChart(values, categories);
        }else if(chartType.equalsIgnoreCase("box")){
            if(canvas.getFrameType() == Canvas.BAR_FRAME || canvas.getFrameType() == Canvas.PIE_FRAME){
                System.out.println("Wrong Diagram type. This Canvas is for BoxCharts");
                return null;
            }
            if(canvas.getFrameType() == Canvas.VIRGIN_FRAME){
                canvas.setFrame(new BoxFrame(canvas.getTitle()));
            }
            returnChart = ((BoxFrame)canvas.getFrame()).addBoxChart(values);
        }else{
            throw new UndefinedOperationException("Chart type not supported. Use bar, pie or box as Chart Type");
        }

        return returnChart;
    }

    @Override
    public Chart addChart(Canvas canvas, String chartType, List<Double> values, String name, List<String> categories) {
        return null;
    }

    @Override
    public void removeGraph(Canvas canvas, Graph graph) throws SetlException {

        canvas.getFrame().removeGraph(graph);
    }

    @Override
    public void labelAxis(Canvas canvas, String xLabel, String yLabel) {
        ((DrawFrame)canvas.getFrame()).setLabel(xLabel, yLabel);
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
        ((DrawFrame)canvas.getFrame()).modyScale(yMin, yMax);
        ((DrawFrame)canvas.getFrame()).modxScale(xMin, xMax);
    }

    @Override
    public void exportCanvas(Canvas canvas, String path) throws FileNotWritableException {
        BufferedImage image = new BufferedImage(canvas.getFrame().getWidth(), canvas.getFrame().getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        canvas.getFrame().paint(graphics2D);
        try {
            ImageIO.write(image, "png", new File(path + ".png"));
        } catch (IOException except) {
            throw new FileNotWritableException("write couldnt be completed", except);
        }

    }

    @Override
    public void modScaleType(Canvas canvas, String xType, String yType) {

        if (xType.equalsIgnoreCase("log")) {
            ((DrawFrame)canvas.getFrame()).setxAxis(new LogarithmicAxis(canvas.getFrame().getxAxis().getLabel()));
        } else if (xType.equalsIgnoreCase("num")) {
            ((DrawFrame)canvas.getFrame()).setxAxis(new NumberAxis(canvas.getFrame().getxAxis().getLabel()));
        } else {
            System.out.println("Wrong x-Axis type, use log or num");
        }
        if (yType.equalsIgnoreCase("log")) {
            ((DrawFrame)canvas.getFrame()).setyAxis(new LogarithmicAxis(canvas.getFrame().getyAxis().getLabel()));
        } else if (yType.equalsIgnoreCase("num")) {
            ((DrawFrame)canvas.getFrame()).setyAxis(new NumberAxis(canvas.getFrame().getyAxis().getLabel()));
        } else {
            System.out.println("Wrong y-Axis type, use log or num");
        }

    }

    @Override
    public Graph addBullets(Canvas canvas, List<List<Double>> bullets) {
        if(canvas.getFrameType() == Canvas.VIRGIN_FRAME){
            canvas.setFrameType(Canvas.DRAW_FRAME);
            canvas.setFrame(new DrawFrame(canvas.getTitle()));
        }
        else if(canvas.getFrameType() >= Canvas.BAR_FRAME){
            System.out.println("Canvas is used for Diagramms. Not possible to insert Graph");
            return null;
        }
        return ((DrawFrame)canvas.getFrame()).addBulletDataset("Bullets", bullets, new ChartColor(0, 0, 0));
    }

    @Override
    public Graph addBullets(Canvas canvas, List<List<Double>> bullets, List<Integer> color) {
        if(canvas.getFrameType() == Canvas.VIRGIN_FRAME){
            canvas.setFrameType(Canvas.DRAW_FRAME);
            canvas.setFrame(new DrawFrame(canvas.getTitle()));
        }
        else if(canvas.getFrameType() >= Canvas.BAR_FRAME){
            System.out.println("Canvas is used for Diagramms. Not possible to insert Graph");
            return null;
        }
        return ((DrawFrame)canvas.getFrame()).addBulletDataset("Bullets", bullets, new ChartColor(color.get(0), color.get(1), color.get(2)));
    }
}
