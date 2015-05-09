package org.randoom.setlx.utilities;

import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;




public class DrawFrame extends JFrame {
    private XYSeriesCollection dataset;
    public DrawFrame()
    {
        super("Graphic output");

        JPanel chartPanel = createChartPanel();
        add(chartPanel, BorderLayout.CENTER);

        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    private JPanel createChartPanel() {
        // creates a line chart object
        // returns the chart panel
    }

    private XYDataset createDataset(String title ) {
        XYSeries series = new XYSeries(title);
        double x = -10;
        for(x<=10){
            series.add(x,calcFuction(x));
            x += 0.1;
        }

    }
}
