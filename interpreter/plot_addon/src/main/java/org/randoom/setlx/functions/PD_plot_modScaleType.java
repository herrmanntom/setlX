package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.*;

import java.util.HashMap;

public class PD_plot_modScaleType extends PreDefinedProcedure {

    private final static ParameterDef CANVAS = createParameter("Canvas");
    private final static ParameterDef XTYPE = createParameter("xType");
    private final static ParameterDef YTYPE = createParameter("yType");

    public final static PreDefinedProcedure DEFINITION = new PD_plot_modScaleType();

    private PD_plot_modScaleType() {
        super();
        addParameter(CANVAS);
        addParameter(XTYPE);
        addParameter(YTYPE);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {

        if (!CheckType.isCanvas(args.get(CANVAS))) {
            throw new UndefinedOperationException("First parameter has to be of object Canvas");
        }


        if (!CheckType.isSetlString(args.get(XTYPE))) {
            throw new UndefinedOperationException("Second parameter xType has to be a String");
        }

        if (!CheckType.isSetlString(args.get(YTYPE))) {
            throw new UndefinedOperationException("Third parameter yType has to be a String");
        }

        final String xType = args.get(XTYPE).toString().replace("\"", "");
        final String yType = args.get(YTYPE).toString().replace("\"", "");

        if (!(xType.equals("num") || xType.equals("log"))) {
            throw new UndefinedOperationException("Second parameter xType has to be either \"num\" or \"log\"");
        }

        if (!(yType.equals("num") || yType.equals("log"))) {
            throw new UndefinedOperationException("Third parameter yType has to be either \"num\" or \"log\"");
        }

        ConnectJFreeChart.getInstance().modScaleType((Canvas) args.get(CANVAS), xType, yType);
        return new SetlString("Set ScaleType x to " + xType + " and y to " + yType);
    }
}
