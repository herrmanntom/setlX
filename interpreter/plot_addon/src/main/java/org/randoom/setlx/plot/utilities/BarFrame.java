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
import org.randoom.setlx.types.Value;

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

    public BarFrame(String title, double width, double height) {
        super(title,width , height);
        this.xAxis = new CategoryAxis();
        this.yAxis = new NumberAxis();
    }

    @Override
    public Value addTextLabel(List<Double> coordinates, String text) {
        Chart chart = new Chart(coordinates, null, text, true);
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
        ArrayList<Chart> func = new ArrayList<Chart>(functions);
        functions.clear();
        for(Chart chart: func){
            this.addBarChart(chart.getValues(), chart.getCategories(), chart.getName());
        }

    }

    public Chart addBarChart(List<Double> values, List<String> categories, String name) {

        for(int i = 0; i < values.size(); i++){
            dataset.addValue(values.get(i), name, categories.get(i));
        }
        renderer.setSeriesPaint(chartCount, getNewColor());
        Chart chart = new Chart(values, categories, name, false);
        plot = new CategoryPlot(dataset, (CategoryAxis)xAxis, (NumberAxis)yAxis, renderer);

        functions.add(chart);
        this.redraw();
        chartCount++;
        return chart;
    }

}
