package org.randoom.setlx.utilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.DefaultPieDataset;
import org.randoom.setlx.exceptions.IllegalRedefinitionException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arne on 03.06.15.
 */
public class PieFrame extends AbstractFrame {
    private PiePlot plot;
    @Override
    protected Plot getPlot() {
        return this.plot;
    }

    @Override
    protected void setPlot(Plot plot) {
        this.plot = (PiePlot)plot;
    }
    private DefaultPieDataset dataset = new DefaultPieDataset();
    private List<Chart> functions = new ArrayList();
    @Override
    protected List getFunctions() {
        return this.functions;
    }

    @Override
    protected void setFunctions(List fun) {
        this.functions = fun;
    }

    @Override
    public Value addTextLabel(List<Double> coordinates, String text) {
        return new SetlString("Text label are not supported in PieCharts");
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
        chartCount = functions.size();
        dataset.clear();
        plot = null;
        ArrayList<Chart> func = new ArrayList<Chart>(functions);
        functions.clear();
        for(Chart chart: func){
            this.addPieChart(chart.getValues(), chart.getCategories());
        }
        this.redraw();
    }
    @Override
    public void setLabel(String xLabel, String yLabel) throws IllegalRedefinitionException {
        throw new IllegalRedefinitionException("Pie Charts doesnt have Axis Labels.");
    }

    public Chart addPieChart(List<Double> values, List<String> categories) {
        dataset.clear();
        functions.clear();
        for(int i = 0; i < values.size(); i++){
            dataset.setValue(categories.get(i), values.get(i));
        }
        Chart chart = new Chart(values, categories, "series"+chartCount, false);
        plot = new PiePlot(dataset);

        functions.add(chart);
        this.redraw();
        return chart;
    }
}
