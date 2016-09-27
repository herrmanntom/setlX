package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.plot.types.Canvas;
import org.randoom.setlx.plot.utilities.ConnectJFreeChart;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

public class PD_plot_createCanvas extends PreDefinedProcedure {

    private final static ParameterDefinition NAME = createOptionalParameter("name", Rational.ONE);

    public final static PreDefinedProcedure
            DEFINITION = new PD_plot_createCanvas();

    private PD_plot_createCanvas() {
        super();
        addParameter(NAME);
    }

    @Override
    protected Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        Value nameV = args.get(NAME);
        Canvas canvas;
        if(!nameV.equalTo(Rational.ONE)){
            canvas = ConnectJFreeChart.getInstance().createCanvas(nameV.getUnquotedString(state));
        } else {
            canvas = ConnectJFreeChart.getInstance().createCanvas();
        }

        // give some time to render the swing UI in the background
        try {
            Thread.sleep(100);
        } catch (final Exception e) {
            // don't care if anything happens here...
        }

        return canvas;
    }
}
