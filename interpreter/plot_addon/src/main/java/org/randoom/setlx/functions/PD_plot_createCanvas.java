package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.ConnectJFreeChart;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

public class PD_plot_createCanvas extends PreDefinedProcedure {


    public final static PreDefinedProcedure
            DEFINITION = new PD_plot_createCanvas();

    private PD_plot_createCanvas() {
        super();
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        return ConnectJFreeChart.getInstance().createCanvas();
    }
}
