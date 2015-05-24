package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Canvas;
import org.randoom.setlx.utilities.ConnectJFreeChart;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

public class PD_plot_defineTitle extends PreDefinedProcedure {

    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef TITLE = createParameter("title");

    public final static PreDefinedProcedure DEFINITION = new PD_plot_defineTitle();

    private PD_plot_defineTitle(){
        super();
        addParameter(CANVAS);
        addParameter(TITLE);
    }
    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        ConnectJFreeChart.getInstance().defineTitle((Canvas) args.get(CANVAS), args.get(TITLE).toString().replace("\"", ""));
        return new SetlString("Added Title \""+args.get(TITLE));
    }
}
