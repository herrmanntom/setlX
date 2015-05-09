package org.randoom.setlx.utilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;


public class DrawFrame extends JFrame {
    private XYSeriesCollection dataset;
    private double x_Min;
    private double x_Max;
    public DrawFrame()
    {
        super("Graphic output");

        JPanel chartPanel = createChartPanel();
        add(chartPanel, BorderLayout.CENTER);

        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        x_Min = -10.0;
        x_Max = 10.0;
    }

    public JPanel createChartPanel(String title, String function) {
        addDataset(title, function);
        JFreeChart chart = ChartFactory.createXYLineChart("Chart title", "X", "Y", this.dataset);
        return new JPanel();
    }

    private void addDataset(String title, String function ) {
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
}
