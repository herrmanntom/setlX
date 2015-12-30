package org.randoom.setlx.plot.utilities;

import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.plot.types.Chart1D;
import org.randoom.setlx.types.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arne on 03.06.15.
 */
public class BarFrame extends AbstractFrame {
    private static final long serialVersionUID = -3517032983648321103L;

    private CategoryPlot plot;

    @Override
    protected Plot getPlot() {
        return plot;
    }

    private DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    private final BarRenderer renderer = new BarRenderer();


    private List<Chart1D> functions = new ArrayList<>();

    @Override
    protected List<Value> getFunctions() {
        return new ArrayList<Value>(this.functions);
    }

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

    public BarFrame(String title, double width, double height) {
        super(title,width , height);
        this.xAxis = new CategoryAxis();
        this.yAxis = new NumberAxis();
    }

    @Override
    public Value addTextLabel(List<Double> coordinates, String text) {
        Chart1D chart = new Chart1D(coordinates, null, text, true);
        CategoryTextAnnotation label = new CategoryTextAnnotation(text,dataset.getColumnKey(coordinates.get(0).intValue()-1) ,coordinates.get(1));
        label.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        functions.add(chart);
        plot.addAnnotation(label);
        this.redraw();
        chartCount++;
        return chart;

    }

    @Override
    public void setLabel(String xLabel, String yLabel) {
        plot.getDomainAxis().setLabel(xLabel);
        plot.getRangeAxis().setLabel(yLabel);
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);
    }

    @Override
    protected void remakeFunctions() throws SetlException {
        chartCount = functions.size();
        dataset.clear();
        plot = new CategoryPlot(dataset, (CategoryAxis)xAxis, (NumberAxis)yAxis, renderer);
        ArrayList<Chart1D> func = new ArrayList<>(functions);
        functions.clear();
        for(Chart1D chart: func){
            this.addBarChart(chart.getValues(), chart.getCategories(), chart.getName());
        }

    }

    public Chart1D addBarChart(List<Double> values, List<String> categories, String name) {

        for(int i = 0; i < values.size(); i++){
            dataset.addValue(values.get(i), name, categories.get(i));
        }
        renderer.setSeriesPaint(chartCount, getNewColor());
        Chart1D chart = new Chart1D(values, categories, name, false);
        plot = new CategoryPlot(dataset, (CategoryAxis)xAxis, (NumberAxis)yAxis, renderer);

        functions.add(chart);
        this.redraw();
        chartCount++;
        return chart;
    }

}
