package org.randoom.setlx.utilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.randoom.setlx.exceptions.SetlException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arne on 03.06.15.
 */
public class BoxFrame extends AbstractFrame {

    private DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
    private final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();

    private List<Chart> functions = new ArrayList<Chart>();
    @Override
    public Graph addTextLabel(List<Double> coordinates, String text) {
        return null;
    }

    public BoxFrame(String title) {
        super(title);
        this.xAxis = new CategoryAxis();
        this.yAxis = new NumberAxis();
        ((NumberAxis)yAxis).setAutoRangeIncludesZero(false);
        renderer.setFillBox(false);
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
    protected void remakeFunctions() throws SetlException {
        dataset.clear();
        for(Chart item: functions){
            this.addBoxChart(item.getValues(), item.getCategories());
        }

    }



    public Chart addBoxChart(List<Double> values, List<String> categories) {
        dataset.add(values, categories.get(0), categories.get(1));
        Chart chart = new Chart(values, categories);
        plot = new CategoryPlot(dataset, (CategoryAxis)xAxis, (NumberAxis)yAxis, renderer);

        functions.add(chart);
        this.redraw();
        return chart;
    }
}
