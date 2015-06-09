package org.randoom.setlx.utilities;

import org.jfree.chart.ChartFactory;
import org.jfree.data.general.DefaultPieDataset;
import org.randoom.setlx.exceptions.SetlException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arne on 03.06.15.
 */
public class PieFrame extends AbstractFrame {
    private DefaultPieDataset dataset = new DefaultPieDataset();
    private Integer seriescount = 0;
    private List<Chart> functions = new ArrayList<Chart>();
    private String title;
    @Override
    public Graph addTextLabel(List<Double> coordinates, String text) {
        return null;
    }

    public PieFrame(String title) {
        super(title);
        this.title = title;
    }

    @Override
    protected void remakeFunctions() throws SetlException {

    }

    public Chart addPieChart(List<Double> values, List<String> categories) {
        for(int i = 0; i < values.size(); i++){
            dataset.addValue(values.get(i), "series"+seriescount.toString(), categories.get(i));
        }
        Chart chart = new Chart(values, categories);
        if (plot == null) {
            plot = new CategoryPlot(dataset, (CategoryAxis)xAxis, (NumberAxis)yAxis, renderer);
        } else {
            ((CategoryPlot)plot).setDataset(chartCount, dataset);
            ((CategoryPlot)plot).setRenderer(chartCount, renderer);
        }
        functions.add(chart);
        chartCount++;
        return chart;
    }
}
