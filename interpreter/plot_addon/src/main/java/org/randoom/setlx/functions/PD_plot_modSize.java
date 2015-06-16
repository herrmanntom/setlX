package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.*;

import java.util.HashMap;
import java.util.List;

/**
 * Created by arne on 16.06.15.
 */
public class PD_plot_modSize extends PreDefinedProcedure {

    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef SIZE = createParameter("size");

    public final static PreDefinedProcedure DEFINITION = new PD_plot_modSize();

    private PD_plot_modSize(){
        super();
        addParameter(CANVAS);
        addParameter(SIZE);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        //modSize(Canvas: canvas, List<Double>: size);
        // initialise parameter canvas and size
        Canvas canvas = (Canvas) args.get(CANVAS);
        SetlList sizeV  = (SetlList) args.get(SIZE);
        List size = ConvertSetlTypes.convertSetlListAsDouble(sizeV);

        ConnectJFreeChart.getInstance().modSize(canvas, size);
        return new SetlString("Set Framesize to " +size.get(0) + " x " + size.get(1));
    }
}
