package org.randoom.setlx.utilities;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.randoom.setlx.exceptions.SetlException;

import java.util.List;

/**
 * Created by arne on 03.06.15.
 */
public class BarFrame extends AbstractFrame {
        private DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        private final BarRenderer renderer = new BarRenderer();
        private Integer seriescount = 0;

    public BarFrame(String title) {
        super(title);
        this.xAxis = new CategoryAxis();
        this.yAxis = new NumberAxis();


    }

    @Override
    public Graph addTextLabel(List<Double> coordinates, String text) {
        return null;
    }

    @Override
    protected void remakeFunctions() throws SetlException {
        
    }

    public Chart addBarChart(List values) {

        for(int i = 0; i < values.size(); i++){
            //dataset.addValue(values.get(i), "series"+seriescount.toString(), "category"+i);

        }
        return null;
    }
}
