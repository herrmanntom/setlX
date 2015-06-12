package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Canvas;
import org.randoom.setlx.utilities.ConnectJFreeChart;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.ConvertSetlTypes;

import java.util.HashMap;
import java.util.List;

public class PD_plot_addPieChart extends PreDefinedProcedure {

    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef VALUES = createParameter("values");
    private final static ParameterDef CATEGORIES = createParameter("categories");
    //private final static ParameterDef NAME = createOptionalParameter("name", Rational.ONE);
    public final static PreDefinedProcedure DEFINITION = new PD_plot_addPieChart();

    private PD_plot_addPieChart() {
        super();
        addParameter(CANVAS);
        addParameter(VALUES);
        addParameter(CATEGORIES);
    //    addParameter(NAME);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        Canvas canvas = (Canvas) args.get(CANVAS);
        SetlList values = (SetlList) args.get(VALUES);
        SetlList categories = (SetlList) args.get(CATEGORIES);
        List valuesList = ConvertSetlTypes.convertSetlListAsDouble(values);
        List categorieList = ConvertSetlTypes.convertSetlListAsString(categories);

        return ConnectJFreeChart.getInstance().addPieChart(canvas, valuesList, categorieList);
    }
}
