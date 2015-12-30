package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.plot.utilities.ConnectJFreeChart;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

public class PD_plot_createCanvas extends PreDefinedProcedure {

    private final static ParameterDef NAME = createOptionalParameter("name", Rational.ONE);


    public final static PreDefinedProcedure
            DEFINITION = new PD_plot_createCanvas();

    private PD_plot_createCanvas() {
        super();
        addParameter(NAME);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDef, Value> args) throws SetlException {
        Value nameV = args.get(NAME);
        if(!nameV.equalTo(Rational.ONE)){
            SetlString nameS =  (SetlString)nameV;
            String name = nameS.toString().replace("\"", "");
            return ConnectJFreeChart.getInstance().createCanvas(name);
        }

        return ConnectJFreeChart.getInstance().createCanvas();
    }
}
