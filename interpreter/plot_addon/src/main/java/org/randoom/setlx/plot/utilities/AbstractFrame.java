package org.randoom.setlx.plot.utilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.title.LegendTitle;
import org.randoom.setlx.exceptions.IllegalRedefinitionException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.Value;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by arne on 03.06.15.
 */
public abstract class AbstractFrame extends JFrame {
    Plot plot;

    protected abstract Plot getPlot();
    ChartPanel chartPanel;
    LegendTitle legend;
    protected double x_Min;
    protected double x_Max;
    protected Axis xAxis;
    protected Axis yAxis;
    protected JPanel jPanel;
    protected int chartCount;
    protected String title;
    protected Dimension dim = new Dimension();

    public boolean isLegendVisible() {
        return legendVisible;
    }

    public void setLegendVisible(boolean legendVisible) {
        this.legendVisible = legendVisible;
    }

    protected boolean legendVisible = true;

    protected Color getNewColor(){
        switch(chartCount % 6){
            case 0: return Color.red;
            case 1: return Color.blue;
            case 2: return Color.green;
            case 3: return Color.cyan;
            case 4: return Color.orange;
            default: return Color.magenta;
        }

    }


    public void modSize(double x, double y){
        dim.setSize(x,y);
        setPreferredSize(dim);
        jPanel.setPreferredSize(dim);
        //chartPanel.setPreferredSize(dim);
        this.redraw();
    }


    protected List<Value> functions;
    protected abstract List<Value> getFunctions();
    protected abstract void setFunctions(List<Value> fun);
    public int getChartCount(){
        return this.chartCount;
    }
    public abstract Value addTextLabel(List<Double> coordinates, String text);

    public abstract void setLabel(String xLabel, String yLabel) throws IllegalRedefinitionException;

    public Axis getyAxis() {
        return yAxis;
    }

    public Axis getxAxis() {
        return xAxis;
    }

    public AbstractFrame(String title, double width, double height){
        super(title);
        this.title = title;
        dim.setSize(width, height);
        this.setVisible(true);
        setLayout(new BorderLayout());
        this.chartCount = 0;
        jPanel = new JPanel();
        jPanel.setName(title);
        jPanel.setLayout(new BorderLayout());
        jPanel.setPreferredSize(dim);
        add(jPanel, BorderLayout.CENTER);
        setPreferredSize(dim);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void setTitle(String title) {
        this.title = title;
        if (chartPanel != null) {
            chartPanel.setName(title);
        }
        redraw();
    }

    protected abstract void remakeFunctions() throws SetlException;

    protected void redraw() {
        if(chartCount != 0) {
            jPanel.remove(chartPanel);
        }
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, this.getPlot(), legendVisible);

        chartPanel = new ChartPanel(chart, true, true, true, true, true);

        jPanel.add(chartPanel);

        this.pack();
    }

    public void removeGraph(Value value) throws SetlException {
        List<Value> func = this.getFunctions();

        boolean ispresent = func.remove(value);
        if (!ispresent) {
            throw new UndefinedOperationException("the Graph or Chart you wanted to delete, does not exist");
        }
        chartCount = 0;
        remakeFunctions();
    }

}
