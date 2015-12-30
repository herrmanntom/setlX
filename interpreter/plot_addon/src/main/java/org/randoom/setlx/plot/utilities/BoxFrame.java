package org.randoom.setlx.plot.utilities;

import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.ui.TextAnchor;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.plot.types.Chart2D;
import org.randoom.setlx.types.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by arne on 03.06.15.
 */
public class BoxFrame extends AbstractFrame {
    private static final long serialVersionUID = 7030553931673985106L;

    private CategoryPlot plot;

    @Override
    protected Plot getPlot() {
        return plot;
    }

    private DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
    private final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();

    private List<Chart2D> functions = new ArrayList<>();


    @Override
    protected List<Value> getFunctions() {
        return new ArrayList<Value>(this.functions);
    }

    @Override
    protected void setFunctions(List<Value> fun) {
        List<Chart2D> functions = new ArrayList<>();
        for (Value value : fun) {
            if (value instanceof Chart2D) {
                functions.add((Chart2D) value);
            }
        }
        this.functions = functions;
    }

    @Override
    public Value addTextLabel(List<Double> coordinates, String text) {
        Chart2D chart = new Chart2D(Arrays.asList(coordinates), null, text, true);
        CategoryTextAnnotation label = new CategoryTextAnnotation(text,dataset.getColumnKey(coordinates.get(0).intValue()-1) ,coordinates.get(1));
        label.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        functions.add(chart);
        plot.addAnnotation(label);
        chartCount++;
        return chart;
    }

    public BoxFrame(String title, double width, double height) {
        super(title, width, height);
        this.xAxis = new CategoryAxis();
        this.yAxis = new NumberAxis();
        ((NumberAxis)yAxis).setAutoRangeIncludesZero(false);
        renderer.setFillBox(false);
    }

    @Override
    protected void remakeFunctions() throws SetlException {
        dataset.clear();

        for(Chart2D item: functions){
            this.addBoxChart(item.getValues(), item.getCategories(), item.getName());
        }

    }

    @Override
    public void setLabel(String xLabel, String yLabel) {
        plot.getDomainAxis().setLabel(xLabel);
        plot.getRangeAxis().setLabel(yLabel);
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);
    }

    public Chart2D addBoxChart(List<List<Double>> values, List<String> categories, String name) {

        Chart2D chart = new Chart2D(values, categories, name, false);
        int i = 0;
        for(List<Double> item : values) {
            dataset.add(item, name, categories.get(i));
            i++;
        }
        renderer.setSeriesPaint(chartCount, getNewColor());
        renderer.setMeanVisible(false);
        plot = new CategoryPlot(dataset, (CategoryAxis) xAxis, (NumberAxis) yAxis, renderer);
        functions.add(chart);
        this.redraw();
        chartCount++;
        return chart;
    }
}
