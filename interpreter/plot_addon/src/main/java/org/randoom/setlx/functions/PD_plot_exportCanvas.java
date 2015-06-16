package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.*;

import java.util.HashMap;

public class PD_plot_exportCanvas extends PreDefinedProcedure {

    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef PATH = createParameter("path");
    public final static PreDefinedProcedure DEFINITION = new PD_plot_exportCanvas();

    private PD_plot_exportCanvas() {
        super();
        addParameter(CANVAS);
        addParameter(PATH);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        if(!CheckType.isCanvas(args.get(CANVAS))){
            throw new UndefinedOperationException("First parameter has to be of object Canvas");
        }

        if(!CheckType.isSetlString(args.get(PATH))){
            throw new UndefinedOperationException("Second parameter has to be a String");
        }

        Canvas canvas = (Canvas)args.get(CANVAS);
        SetlString path = (SetlString)args.get(PATH);
        String pathString = path.toString().replace("\"", "");
        ConnectJFreeChart.getInstance().exportCanvas(canvas, pathString);
        return new SetlString("Exported Canvas to "+pathString);
    }
}
