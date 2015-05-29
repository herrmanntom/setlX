package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.*;

import java.util.HashMap;
import java.util.List;

public class PD_plot_addLabel extends PreDefinedProcedure {

    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef XYTUPEL = createParameter("XYTupel");
    private final static ParameterDef LABEL = createParameter("Label");
    public final static PreDefinedProcedure DEFINITION = new PD_plot_addLabel();

    private PD_plot_addLabel() {
        super();
        addParameter(CANVAS);
        addParameter(XYTUPEL);
        addParameter(LABEL);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        Canvas canvas = (Canvas)args.get(CANVAS);
        SetlList xyTupel = (SetlList)args.get(XYTUPEL);
        List<Double> list = ConvertSetlTypes.convertSetlListAsDouble(xyTupel);
        SetlString label = (SetlString)args.get(LABEL);
        String stringLabel = label.toString().replace("\"", "");

        return ConnectJFreeChart.getInstance().addLabel(canvas, list, stringLabel);
    }
}
