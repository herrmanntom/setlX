package org.randoom.setlx.utilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.randoom.setlx.exceptions.SetlException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arne on 03.06.15.
 */
public class PieFrame extends AbstractFrame {
    private DefaultPieDataset dataset = new DefaultPieDataset();
    private List<Chart> functions = new ArrayList();
    @Override
    public Graph addTextLabel(List<Double> coordinates, String text) {
        return null;
    }

    public PieFrame(String title) {
        super(title);
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

    }

    public Chart addPieChart(List<Double> values, List<String> categories) {
        for(int i = 0; i < values.size(); i++){
            dataset.setValue(categories.get(i), values.get(i));
        }
        Chart chart = new Chart(values, categories);
        if (plot == null) {
            plot = new PiePlot(dataset);
        } else {
            ((PiePlot)plot).setDataset(dataset);
        }
        functions.add(chart);
        this.redraw();
        return chart;
    }
}
