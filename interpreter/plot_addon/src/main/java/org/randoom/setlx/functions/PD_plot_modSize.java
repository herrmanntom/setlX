package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.*;

import java.util.HashMap;
import java.util.List;

public class PD_plot_modSize extends PreDefinedProcedure {

    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef SIZE = createParameter("size");

    public final static PreDefinedProcedure DEFINITION = new PD_plot_modSize();

    private PD_plot_modSize() {
        super();
        addParameter(CANVAS);
        addParameter(SIZE);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {

        if (!PlotCheckType.isCanvas(args.get(CANVAS))) {
            throw new UndefinedOperationException("First parameter has to be of object Canvas");
        }

        if (!PlotCheckType.isSetlList(args.get(SIZE))) {
            throw new UndefinedOperationException("Second parameter has to be a Tupel (eq. [800, 600])");
        }

        Canvas canvas = (Canvas) args.get(CANVAS);
        SetlList sizeV = (SetlList) args.get(SIZE);
        if (sizeV.size() != 2) {
            throw new UndefinedOperationException("Second parameter has to be a Tupel (eq. [800, 600])");
        }

        List size = ConvertSetlTypes.convertSetlListAsDouble(sizeV);

        ConnectJFreeChart.getInstance().modSize(canvas, size);
        return new SetlString("Set Framesize to " + size.get(0) + " x " + size.get(1));
    }
}
