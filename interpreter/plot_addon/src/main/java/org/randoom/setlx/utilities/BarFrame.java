package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;

import java.util.List;

/**
 * Created by arne on 03.06.15.
 */
public class BarFrame extends AbstractFrame {

    public BarFrame(String title) {
        super(title);
    }

    @Override
    public Graph addTextLabel(List<Double> coordinates, String text) {
        return null;
    }

    @Override
    protected void remakeFunctions() throws SetlException {
        
    }

    public Chart addBarChart(List values) {
        return null;
    }
}
