package org.randoom.setlx.plot.utilities;

import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.DefaultPieDataset;
import org.randoom.setlx.exceptions.IllegalRedefinitionException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.plot.types.Chart1D;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arne on 03.06.15.
 */
public class PieFrame extends AbstractFrame {
    private static final long serialVersionUID = 3907053936452489199L;

    private PiePlot plot;
    @Override
    protected Plot getPlot() {
        return this.plot;
    }

    private DefaultPieDataset dataset = new DefaultPieDataset();
    private List<Chart1D> functions = new ArrayList<>();
    @Override
    protected List<Value> getFunctions() {
        return new ArrayList<Value>(this.functions);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setFunctions(List<Value> fun) {
        List<Chart1D> functions = new ArrayList<>();
        for (Value value : fun) {
            if (value instanceof Chart1D) {
                functions.add((Chart1D) value);
            }
        }
        this.functions = functions;
    }

    @Override
    public Value addTextLabel(List<Double> coordinates, String text) {
        return new SetlString("Text label are not supported in PieCharts");
    }

    public PieFrame(String title, double width, double height) {
        super(title,width ,height );
    }

    @Override
    protected void remakeFunctions() throws SetlException {
        chartCount = functions.size();
        dataset.clear();
        plot = null;
        ArrayList<Chart1D> func = new ArrayList<>(functions);
        functions.clear();
        for(Chart1D chart: func){
            this.addPieChart(chart.getValues(), chart.getCategories());
        }
        this.redraw();
    }
    @Override
    public void setLabel(String xLabel, String yLabel) throws IllegalRedefinitionException {
        throw new IllegalRedefinitionException("Pie Charts doesnt have Axis Labels.");
    }

    public Chart1D addPieChart(List<Double> values, List<String> categories) {
        dataset.clear();
        functions.clear();
        for(int i = 0; i < values.size(); i++){
            dataset.setValue(categories.get(i), values.get(i));
        }
        Chart1D chart = new Chart1D(values, categories, "series"+chartCount, false);
        plot = new PiePlot(dataset);

        functions.add(chart);
        this.redraw();
        chartCount++;
        return chart;
    }
}
