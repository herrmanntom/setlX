package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;

import java.util.List;

/**
 * Created by arne on 03.06.15.
 */
public class PieFrame extends AbstractFrame {
    @Override
    public Graph addTextLabel(List<Double> coordinates, String text) {
        return null;
    }

    public PieFrame(String title) {
        super(title);
    }

    @Override
    protected void remakeFunctions() throws SetlException {

    }

    public Chart addPieChart(List values) {
        return null;
    }
}
