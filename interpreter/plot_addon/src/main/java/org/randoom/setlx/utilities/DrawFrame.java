package org.randoom.setlx.utilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;


public class DrawFrame extends JFrame {
    private XYSeriesCollection dataset;
    private double x_Min;
    private double x_Max;#
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        xAxis = "X";
        yAxis = "Y";
        chartTitle = "Chart Title";
        x_Min = -10.0;
        x_Max = 10.0;
    }

    public void addDataset(String title, String function ) {
        boolean autoSort = true;
        boolean allowDuplicateXValues = false;
        XYSeries series = new XYSeries(title, autoSort, allowDuplicateXValues);
        CalcFunction calc = new CalcFunction(function);
        double x = x_Min;
        for(x<=x_Max){
            series.add(x,calc.calcYfromX(x));
            x += 0.1;
        }
        dataset.addSeries(series);
    }

    public void redraw(){
        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxis, yAxis, this.dataset);
        jPanel = new ChartPanel(chart);
        /*
        if(chartPanel == null) {
            chartPanel = new ChartPanel(chart);
            jPanel.add(chartPanel, BorderLayout.CENTER);
            jPanel.validate();
        }
        */
    }
}
