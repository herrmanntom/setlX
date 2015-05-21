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

public class PD_plot_addChart extends PreDefinedProcedure {

    private final static ParameterDef CANVAS = createParameter("canvas");
    private final static ParameterDef CHARTTYPE = createParameter("chartType");
    private final static ParameterDef VALUES = createParameter("values");
    private final static ParameterDef NAME = createOptionalParameter("name", Rational.ONE);
    public final static PreDefinedProcedure DEFINITION = new PD_plot_addChart();
    private PD_plot_addChart(){
        super();
        addParameter(CANVAS);
        addParameter(CHARTTYPE);
        addParameter(VALUES);
        addParameter(NAME);
    }
    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        Canvas canvas = (Canvas)args.get(CANVAS);
        SetlString chartType = (SetlString)args.get(CHARTTYPE);
        String chartTypeString = chartType.toString().replace("\"", "");;
        SetlList values = (SetlList)args.get(VALUES);
        List valuesList = ConvertSetlTypes.convertSetlListAsDouble(values);
        Value name = args.get(NAME);

        if(!name.equalTo(Rational.ONE)){
            SetlString nameSetlString = (SetlString)name;
            String nameString = nameSetlString.toString();

            return ConnectJFreeChart.getInstance().addChart(canvas, chartTypeString, valuesList, nameString);
        }

        return ConnectJFreeChart.getInstance().addChart(canvas, chartTypeString, valuesList);
    }
}
