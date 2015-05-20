package org.randoom.setlx.utilities;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class DrawFrame extends JFrame {
    private XYSeriesCollection dataset = new XYSeriesCollection();
    private double x_Min;
    private double x_Max;
    private double y_Min;
    private double y_Max;

    private List<Plotfunction> functions = new ArrayList<>();
    private ValueAxis xAxis;
    private ValueAxis yAxis;
    private JPanel jPanel;
    private XYPlot plot;
    private int chartCount;
    private JFreeChart chart;
    private ChartPanel chartPanel;

    public void setxAxis(ValueAxis xAxis) {
        this.xAxis = xAxis;
        if(plot !=null){
            plot.setDomainAxis(this.xAxis);
        }
    }

    public void modxScale(double x_Min, double x_Max){
        this.x_Max = x_Max;
        this.x_Min = x_Min;
        ValueAxis axis = plot.getDomainAxis();
        axis.setLowerBound(this.x_Min);
        axis.setUpperBound(this.x_Max);
        if(chartCount != 0) {
            chartCount = 0;
            jPanel.remove(chartPanel);
            List<Plotfunction> func = new ArrayList<>(functions);
            functions.clear();
            for(Plotfunction item:func){
                if(!item.getFunctionstring().isEmpty()){
                    this.addDataset(item.getTitle(), item.getFunctionstring(), item.isArea(), item.getColor());
                }
                else if(!item.getXfunction().isEmpty()){
                    this.addParamDataset(item.getTitle(), item.getXfunction(), item.getYfunction(), item.isArea(), item.getColor());
                }
                else if(item.getFunction() != null){
                    this.addListDataset(item.getTitle(), item.getFunction(), item.isArea(), item.getColor());
                }
            }
        }
    }
    public void modyScale(double y_Min, double y_Max){
        this.y_Max = y_Max;
        this.y_Min = y_Min;
        ValueAxis axis = plot.getRangeAxis();
        axis.setLowerBound(this.y_Min);
        axis.setUpperBound(this.y_Max);
    }

    public void setyAxis(ValueAxis yAxis) {
        this.yAxis = yAxis;
        if(plot !=null){
            plot.setRangeAxis(this.yAxis);
        }
    }
    public DrawFrame(String title) {
        super(title);
        chartCount = 0;
        jPanel = new JPanel();
        jPanel.setName(title);
        add(jPanel, BorderLayout.CENTER);
        setSize(640, 480);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        x_Min = -10.0;
        x_Max = 10.0;
        xAxis = new NumberAxis("x");
        yAxis = new NumberAxis("y");
        plot = null;
    }

    public XYSeries addDataset(String title, String function, boolean area, ChartColor color) {
        Plotfunction plotfun = new Plotfunction(title, area);
        plotfun.setFunctionstring(function);
        functions.add(plotfun);
        XYSeries series = new XYSeries(title, true, false);
        CalcFunction calc = new CalcFunction(function);
        XYItemRenderer renderer;
        if(area){
            renderer = new XYDifferenceRenderer();
        }
        else{
            renderer = new XYLineAndShapeRenderer(true, false);
        }
        renderer.setSeriesPaint(0, color);
        double x = x_Min;
        double step = (x_Max - x_Min)/200;
        while(x<=x_Max){
            series.add(x,calc.calcYfromX(x));
            x += step;
        }
        XYSeriesCollection col = new XYSeriesCollection(series);

        if(plot == null){
            plot = new XYPlot(col, xAxis, yAxis, renderer);
        }
        else{
            plot.setDataset(chartCount, col);
            plot.setRenderer(chartCount, renderer);
        }
        this.redraw();
        return series;
    }

    private void redraw(){
        if(chartCount != 0) {
            jPanel.remove(chartPanel);
        }
        chart = new JFreeChart("title", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        chartPanel = new ChartPanel(chart, true, true, true, true, true);

        jPanel.add(chartPanel);

        this.pack();
        chartCount++;
    }
    public XYSeries addListDataset(String title, List<List<Double>> function, boolean area, ChartColor color){
        Plotfunction plotfun = new Plotfunction(title, area);
        plotfun.setFunction(function);
        functions.add(plotfun);
        XYSeries series = new XYSeries(title, false, true);
        XYItemRenderer renderer;
        if(area){
            renderer = new XYDifferenceRenderer();
        }
        else{
            renderer = new XYLineAndShapeRenderer(true, false);
        }
        renderer.setSeriesPaint(0, color);
        for(List<Double> element: function){
            series.add(element.get(0), element.get(1));
        }
        XYSeriesCollection col = new XYSeriesCollection(series);

        if(plot == null){
            plot = new XYPlot(col, xAxis, yAxis, renderer);
        }
        else{
            plot.setDataset(chartCount, col);
            plot.setRenderer(chartCount, renderer);
        }
        this.redraw();
        return series;
    }

    public XYSeries addParamDataset(String title, String xfunction, String yfunction, boolean area, ChartColor color){
        Plotfunction plotfun = new Plotfunction(title, area);
        plotfun.setXfunction(xfunction);
        plotfun.setYfunction(yfunction);
        functions.add(plotfun);
        XYSeries series = new XYSeries(title, true, false);
        CalcFunction xcalc = new CalcFunction(xfunction);
        CalcFunction ycalc = new CalcFunction(yfunction);
        XYItemRenderer renderer;
        if(area){
            renderer = new XYDifferenceRenderer();
        }
        else{
            renderer = new XYLineAndShapeRenderer(true, false);
        }
        renderer.setSeriesPaint(0, color);
        for(double x=-50; x<=50;x+=0.1){
            series.add(xcalc.calcYfromX(x),ycalc.calcYfromX(x));
        }
        XYSeriesCollection col = new XYSeriesCollection(series);

        if(plot == null){
            plot = new XYPlot(col, xAxis, yAxis, renderer);
        }
        else{
            plot.setDataset(chartCount, col);
            plot.setRenderer(chartCount, renderer);
        }
        this.redraw();
        return series;
    }

    private class Plotfunction{
        public boolean isArea() {
            return area;
        }

        private boolean area;

        public String getTitle() {
            return title;
        }

        public Plotfunction(String title, boolean area) {
            this.title = title;
            this.area = area;
        }

        public String getXfunction() {
            return xfunction;
        }

        public List<List<Double>> getFunction() {
            return function;
        }

        public String getYfunction() {
            return yfunction;
        }

        private String title;

        private String functionstring = "";

        public String getFunctionstring() {
            return functionstring;
        }

        private ChartColor color;

        public ChartColor getColor() {
            return color;
        }

        public void setColor(ChartColor color) {
            this.color = color;
        }

        public void setFunctionstring(String functionstring) {
            this.functionstring = functionstring;
        }

        public void setFunction(List<List<Double>> function) {
            this.function = function;
        }

        public void setXfunction(String xfunction) {
            this.xfunction = xfunction;
        }

        public void setYfunction(String yfunction) {
            this.yfunction = yfunction;
        }

        private List<List<Double>> function = null;

        private String xfunction = "";

        private String yfunction = "";
    }
}
