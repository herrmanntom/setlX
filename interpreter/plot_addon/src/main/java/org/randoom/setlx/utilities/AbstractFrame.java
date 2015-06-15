package org.randoom.setlx.utilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.title.LegendTitle;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.Value;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arne on 03.06.15.
 */
public abstract class AbstractFrame extends JFrame {
    Plot plot;

    protected abstract Plot getPlot();
    protected abstract void setPlot(Plot plot);
    ChartPanel chartPanel;
    LegendTitle legend;
    protected double x_Min;
    protected double x_Max;
    protected Axis xAxis;
    protected Axis yAxis;
    protected JPanel jPanel;
    protected int chartCount;
    protected String title;



    protected List<Value> functions;
    protected abstract List getFunctions();
    protected abstract void setFunctions(List fun);
    public int getChartCount(){
        return this.chartCount;
    }
    public abstract Value addTextLabel(List<Double> coordinates, String text);

    public Axis getyAxis() {
        return yAxis;
    }

    public Axis getxAxis() {
        return xAxis;
    }

    public AbstractFrame(String title){
        super(title);
        this.setVisible(true);
        this.chartCount = 0;
        jPanel = new JPanel();
        jPanel.setName(title);
        add(jPanel, BorderLayout.CENTER);
        setSize(640, 480);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void setTitle(String title) {
        this.title = title;
        if (chartPanel != null) {
            chartPanel.setName(title);
        }
    }

    protected abstract void remakeFunctions() throws SetlException;

    protected void redraw() {
        if(chartCount != 0) {
            jPanel.remove(chartPanel);
        }
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, this.getPlot(), true);

        chartPanel = new ChartPanel(chart, true, true, true, true, true);

        jPanel.add(chartPanel);

        this.pack();
        chartCount++;
    }

    public void removeGraph(Value value) throws SetlException {
        List func = this.getFunctions();

        boolean ispresent = func.remove(value);
        if (!ispresent) {
            throw new UndefinedOperationException("the Graph or Chart you wanted to delete, does not exist");
        }

        remakeFunctions();
    }

}
