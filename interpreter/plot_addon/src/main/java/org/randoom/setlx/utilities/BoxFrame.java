package org.randoom.setlx.utilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.randoom.setlx.exceptions.SetlException;

import java.util.List;

/**
 * Created by arne on 03.06.15.
 */
public class BoxFrame extends AbstractFrame {
    @Override
    public Graph addTextLabel(List<Double> coordinates, String text) {
        return null;
    }

    public BoxFrame(String title) {
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

    public Chart addBoxChart(List values) {
        return null;
    }
}
