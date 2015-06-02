package org.randoom.setlx.utilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.*;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.randoom.setlx.exceptions.SetlException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class DrawFrame extends JFrame {
    XYPlot plot;
    ChartPanel chartPanel;
    LegendTitle legend;
    private double x_Min;
    private double x_Max;
    private List<Graph> functions = new ArrayList<Graph>();
    private ValueAxis xAxis;
    private ValueAxis yAxis;
    private JPanel jPanel;
    private int chartCount;
    private String title = "title";

    public ValueAxis getyAxis() {
        return yAxis;
    }

    public ValueAxis getxAxis() {
        return xAxis;
    }

    public DrawFrame(String title) {
        super(title);
        chartCount = 0;
        jPanel = new JPanel();
        jPanel.setName(title);
        add(jPanel, BorderLayout.CENTER);
        setSize(640, 480);

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        x_Min = -10.0;
        x_Max = 10.0;
        xAxis = new NumberAxis("x");
        yAxis = new NumberAxis("y");
        plot = null;
    }

    public void setTitle(String title) {
        this.title = title;
        if(chartPanel != null){
            chartPanel.setName(title);
        }
    }

    public void setLabel(String xLabel, String yLabel) {
        plot.getDomainAxis().setLabel(xLabel);
        plot.getRangeAxis().setLabel(yLabel);
    }

    public void setxAxis(ValueAxis xAxis) {
        this.xAxis = xAxis;
        if (plot != null) {
            plot.setDomainAxis(this.xAxis);
        }
    }

    public void modxScale(double x_Min, double x_Max) throws SetlException {
        this.x_Max = x_Max;
        this.x_Min = x_Min;
        ValueAxis axis = plot.getDomainAxis();
        axis.setLowerBound(this.x_Min);
        axis.setUpperBound(this.x_Max);
        remakeFunctions();
    }

    private void remakeFunctions() throws SetlException{

        plot = new XYPlot(new XYSeriesCollection(), xAxis, yAxis, new XYLineAndShapeRenderer());
        this.redraw();
        ArrayList<Graph> func = new ArrayList<Graph>(functions);
        functions.clear();
        for (Graph item : func) {
            if (!item.getFunctionstring().isEmpty()) {
                this.addDataset(item.getTitle(), item.getFunctionstring(), item.getInterpreterState() , item.isArea(), item.getColor());
            } else if (!item.getXfunction().isEmpty()) {
                this.addParamDataset(item.getTitle(), item.getXfunction(), item.getYfunction(), item.getInterpreterState(), item.isArea(), item.getColor(), item.getCoordinates());
            } else if (item.getFunction() != null) {
                if (item.isBullets()) {
                    this.addBulletDataset(item.getTitle(), item.getFunction(), item.getColor());
                } else {
                    this.addListDataset(item.getTitle(), item.getFunction(), item.isArea(), item.getColor());
                }
            } else if(item.getCoordinates() != null){
                this.addTextLabel(item.getCoordinates(), item.getTitle());
            } else{
                System.out.println("Something went horribly wrong. \n If you get the error, contact us, get a coffee and wait for a solution");
            }
        }
        this.redraw();
        chartCount--;
    }


    public void modyScale(double y_Min, double y_Max) {
        ValueAxis axis = plot.getRangeAxis();
        axis.setLowerBound(y_Min);
        axis.setUpperBound(y_Max);
    }

    public void setyAxis(ValueAxis yAxis) {
        this.yAxis = yAxis;
        if (plot != null) {
            plot.setRangeAxis(this.yAxis);
        }
    }

    public Graph addDataset(String title, String function, State interpreterState, boolean area, Color color) throws SetlException {

        Graph plotfun = new Graph(title, area, interpreterState);
        plotfun.setFunctionstring(function);
        functions.add(plotfun);
        XYSeries series = new XYSeries(title, true, false);
        CalcFunction calc = new CalcFunction(function, interpreterState);
        XYItemRenderer renderer;
        if (area) {
            renderer = new XYAreaRenderer(XYAreaRenderer.AREA);
            ((XYAreaRenderer) renderer).setOutline(true);
        } else {
            renderer = new XYLineAndShapeRenderer(true, false);
        }
        renderer.setSeriesPaint(0, color);
        double x = x_Min;
        double step = (x_Max - x_Min) / 200;
        while (x <= x_Max) {
            series.add(x, calc.calcYfromX(x));
            x += step;
        }
        XYSeriesCollection col = new XYSeriesCollection(series);
        if (plot == null) {
            plot = new XYPlot(col, xAxis, yAxis, renderer);
        } else {
            plot.setDataset(chartCount, col);
            plot.setRenderer(chartCount, renderer);
        }
        this.redraw();
        return plotfun;
    }

    private void redraw() {
        if (chartCount != 0) {
            jPanel.remove(chartPanel);
        }
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);

        chartPanel = new ChartPanel(chart, true, true, true, true, true);

        jPanel.add(chartPanel);

        this.pack();
        chartCount++;
    }

    public Graph addListDataset(String title, List<List<Double>> function, boolean area, Color color) {
        Graph plotfun = new Graph(title, area, new State());
        plotfun.setFunction(function);
        plotfun.setBullets(false);
        functions.add(plotfun);

        XYSeries series = new XYSeries(title, false, true);
        XYItemRenderer renderer;
        if (area) {
            renderer = new XYSplineRenderer(1, XYSplineRenderer.FillType.TO_ZERO);
        } else {
            renderer = new XYSplineRenderer(1);
        }
        renderer.setSeriesPaint(0, color);
        for (List<Double> element : function) {
            series.add(element.get(0), element.get(1));
        }
        XYSeriesCollection col = new XYSeriesCollection(series);

        if (plot == null) {
            plot = new XYPlot(col, xAxis, yAxis, renderer);
        } else {
            plot.setDataset(chartCount, col);
            plot.setRenderer(chartCount, renderer);
        }
        this.redraw();
        return plotfun;
    }
    public Graph addTextLabel(List<Double> coordinates, String text){
        Graph labelGraph = new Graph(text, false, new State());
        labelGraph.setCoordinates(coordinates);
        functions.add(labelGraph);
        XYTextAnnotation label = new XYTextAnnotation(text, coordinates.get(0), coordinates.get(1));
        if (plot == null) {
            plot = new XYPlot();
            plot.setDomainAxis(xAxis);
            plot.setRangeAxis(yAxis);
        }
        plot.addAnnotation(label);
        this.redraw();
        return labelGraph;
    }
    public Graph addBulletDataset(String title, List<List<Double>> bullets, Color color) {

        Graph plotfun = new Graph(title, false, new State());
        plotfun.setFunction(bullets);
        plotfun.setBullets(true);
        functions.add(plotfun);
        XYSeries series = new XYSeries(title, false, true);
        XYDotRenderer renderer = new XYDotRenderer();
        renderer.setDotHeight(5);
        renderer.setDotWidth(5);
        renderer.setSeriesPaint(0, color);
        renderer.setSeriesVisibleInLegend(0, false);
        for (List<Double> element : bullets) {
            series.add(element.get(0), element.get(1));
        }
        XYSeriesCollection col = new XYSeriesCollection(series);

        if (plot == null) {
            plot = new XYPlot(col, xAxis, yAxis, renderer);
        } else {
            plot.setDataset(chartCount, col);
            plot.setRenderer(chartCount, renderer);
        }

        this.redraw();
        return plotfun;
    }

    public Graph addParamDataset(String title, String xfunction, String yfunction, State interpreterState, boolean area, Color color, List<Double> limits) throws SetlException {
        Graph plotfun = new Graph(title, area, interpreterState);
        plotfun.setXfunction(xfunction);
        plotfun.setYfunction(yfunction);
        plotfun.setCoordinates(limits);
        functions.add(plotfun);
        XYSeries series = new XYSeries(title, true, false);
        CalcFunction xcalc = new CalcFunction(xfunction, interpreterState);
        CalcFunction ycalc = new CalcFunction(yfunction, interpreterState);
        XYItemRenderer renderer;
        if (area) {
            renderer = new XYDifferenceRenderer();
        } else {
            renderer = new XYLineAndShapeRenderer(true, false);
        }
        renderer.setSeriesPaint(0, color);
        double step = (limits.get(1)- limits.get(0))/200;
        for (double x = limits.get(0); x <= limits.get(1); x += step) {
            series.add(xcalc.calcYfromX(x), ycalc.calcYfromX(x));
        }
        XYSeriesCollection col = new XYSeriesCollection(series);

        if (plot == null) {
            plot = new XYPlot(col, xAxis, yAxis, renderer);
        } else {
            plot.setDataset(chartCount, col);
            plot.setRenderer(chartCount, renderer);
        }
        this.redraw();
        return plotfun;
    }

    public void removeGraph(Graph graph) throws SetlException {
        boolean ispresent = functions.remove(graph);
        if(!ispresent){
            System.out.println("nicht gelöscht");
        }
        remakeFunctions();
    }
}
