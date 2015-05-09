package org.randoom.setlx.utilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;


public class DrawFrame extends JFrame {
    private XYSeriesCollection dataset;
    private String chartTitle = "Chart title";
    private String xAxisLabel = "X";
    private String yAxisLabel = "Y";
    public DrawFrame()
    {
        super("Graphic output");

        JPanel chartPanel = createChartPanel();
        add(chartPanel, BorderLayout.CENTER);

        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public JPanel createChartPanel() {
       // addDataset(title);
        JFreeChart chart = ChartFactory.createXYLineChart("Chart title", "X", "Y", this.dataset);
        //chart.a
        return new JPanel();
    }

    private void addDataset(String title ) {
        boolean autoSort = true;
        boolean allowDuplicateXValues = false;
        XYSeries series = new XYSeries(title, autoSort, allowDuplicateXValues);
        double x = -10;
        /*
        for(x<=10){
            series.add(x,calcFuction(x));
            x += 0.1;
        }
        */
        dataset.addSeries(series);
    }
}
