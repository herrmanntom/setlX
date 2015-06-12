package org.randoom.setlx.utilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.randoom.setlx.exceptions.SetlException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arne on 03.06.15.
 */
public class BarFrame extends AbstractFrame {
    private CategoryPlot plot;
    @Override
    protected Plot getPlot() {
        return plot;
    }

    @Override
    protected void setPlot(Plot plot) {
        this.plot = (CategoryPlot)plot;
    }

    private DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    private final BarRenderer renderer = new BarRenderer();


    private List<Chart> functions = new ArrayList<Chart>();
    @Override
    protected List getFunctions() {
        return this.functions;
    }

    @Override
    protected void setFunctions(List fun) {
        this.functions = fun;
    }

    public BarFrame(String title) {
        super(title);
        this.xAxis = new CategoryAxis();
        this.yAxis = new NumberAxis();
    }

    protected void redraw() {
        if (chartCount != 0) {
            jPanel.remove(chartPanel);
        }
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, this.plot, true);

        chartPanel = new ChartPanel(chart, true, true, true, true, true);

        jPanel.add(chartPanel);

        this.pack();
        chartCount++;
    }


    @Override
    public Graph addTextLabel(List<Double> coordinates, String text) {
        return null;
    }

    @Override
    protected void remakeFunctions() throws SetlException {
        chartCount = functions.size();
        dataset.clear();
        plot = new CategoryPlot(dataset, (CategoryAxis)xAxis, (NumberAxis)yAxis, renderer);
        ArrayList<Chart> func = new ArrayList<Chart>(functions);
        functions.clear();
        for(Chart chart: func){
            this.addBarChart(chart.getValues(), chart.getCategories(), chart.getName());
        }
        this.redraw();

    }

    public Chart addBarChart(List<Double> values, List<String> categories, String name) {

        for(int i = 0; i < values.size(); i++){
            dataset.addValue(values.get(i), name, categories.get(i));
        }
        Chart chart = new Chart(values, categories, name);
        plot = new CategoryPlot(dataset, (CategoryAxis)xAxis, (NumberAxis)yAxis, renderer);

        functions.add(chart);
        this.redraw();
        return chart;
    }
}
