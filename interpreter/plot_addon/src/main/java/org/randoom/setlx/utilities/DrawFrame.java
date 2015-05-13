package org.randoom.setlx.utilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class DrawFrame extends JFrame {
    private XYSeriesCollection dataset = new XYSeriesCollection();
    private double x_Min;
    private double x_Max;
    private String xAxis;
    private String yAxis;
    private String chartTitle;
    private JPanel jPanel;
    //private ChartPanel chartPanel = null;

    public DrawFrame() {
        super("Graphic output");

        jPanel = new JPanel();
        add(jPanel, BorderLayout.CENTER);

        setSize(640, 480);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        xAxis = "X";
        yAxis = "Y";
        chartTitle = "Chart Title";
        x_Min = -10.0;
        x_Max = 10.0;
    }

    public XYSeries addDataset(String title, String function ) {
        XYSeries series = new XYSeries(title, true, false);
        CalcFunction calc = new CalcFunction(function);
        double x = x_Min;
        while(x<=x_Max){
            series.add(x,calc.calcYfromX(x));
            x += 0.1;
        }
        dataset.addSeries(series);

        return series;
    }

    public void redraw(){
        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxis, yAxis, this.dataset);
        jPanel = new ChartPanel(chart);
        jPanel.setVisible(true);

        /*
        if(chartPanel == null) {
            chartPanel = new ChartPanel(chart);
            jPanel.add(chartPanel, BorderLayout.CENTER);
            jPanel.validate();
        }
        */
    }
    public XYSeries addListDataset(String title, List<List<Double>> function){
        XYSeries series = new XYSeries(title, false, true);
        for(List<Double> element: function){
            series.add(element.get(0), element.get(1));
        }
        dataset.addSeries(series);
        return series;
    }

    public XYSeries addParamDataset(String title, String xfunction, String yfunction){
        XYSeries series = new XYSeries(title, true, false);
        CalcFunction xcalc = new CalcFunction(xfunction);
        CalcFunction ycalc = new CalcFunction(yfunction);
        for(double x=-50; x<=50;x+=0.1){
            series.add(xcalc.calcYfromX(x),ycalc.calcYfromX(x));
        }
        dataset.addSeries(series);
        return series;
    }
}
