package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.*;

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
        if(!PlotCheckType.isCanvas(args.get(CANVAS))){
            throw new UndefinedOperationException("First parameter has to be of object Canvas");
        }

        if(!PlotCheckType.isSetlString(args.get(TITLE))){
            throw new UndefinedOperationException("Second parameter has to be a String");
        }

        ConnectJFreeChart.getInstance().defineTitle((Canvas) args.get(CANVAS), args.get(TITLE).toString().replace("\"", ""));
        return new SetlString("Added Title \""+args.get(TITLE));
    }
}
