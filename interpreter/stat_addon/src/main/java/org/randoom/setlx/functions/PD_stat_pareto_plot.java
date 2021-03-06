package org.randoom.setlx.functions;

import org.apache.commons.math3.distribution.ParetoDistribution;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.plot.types.Canvas;
import org.randoom.setlx.plot.utilities.ConnectJFreeChart;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Checker;
import org.randoom.setlx.utilities.Defaults;
import org.randoom.setlx.utilities.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * stat_pareto_plot(scale, shape, canvas):
 *      Plots the probability density function for pareto distribution with scale and shape on a given canvas.
 */
public class PD_stat_pareto_plot extends PreDefinedProcedure {

    private final static ParameterDefinition CANVAS      = createParameter("canvas");
    private final static ParameterDefinition SCALE       = createParameter("scale");
    private final static ParameterDefinition SHAPE       = createParameter("shape");
    private final static ParameterDefinition COLOR       = createOptionalParameter("color", new SetlString("DEFAULT_COLOR"));
    private final static ParameterDefinition LOWER_BOUND = createOptionalParameter("lowerBound", Defaults.createSetlDoubleValue(0.0));
    private final static ParameterDefinition INTERVAL    = createOptionalParameter("interval", Defaults.getDefaultPlotInterval());
    private final static ParameterDefinition UPPER_BOUND = createOptionalParameter("upperBound", Defaults.createSetlDoubleValue(10.0));

    /** Definition of the PreDefinedProcedure 'stat_pareto_plot' */
    public final static PreDefinedProcedure DEFINITION = new PD_stat_pareto_plot();

    private PD_stat_pareto_plot() {
        super();
        addParameter(CANVAS);
        addParameter(SCALE);
        addParameter(SHAPE);
        addParameter(COLOR);
        addParameter(LOWER_BOUND);
        addParameter(INTERVAL);
        addParameter(UPPER_BOUND);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value canvas     = args.get(CANVAS);
        final Value scale      = args.get(SCALE);
        final Value shape      = args.get(SHAPE);
        final Value color      = args.get(COLOR);
        final Value lowerBound = args.get(LOWER_BOUND);
        final Value interval   = args.get(INTERVAL);
        final Value upperBound = args.get(UPPER_BOUND);

        Checker.checkIfCanvas(state, canvas);
        Checker.checkIfNumber(state, lowerBound, upperBound);
        Checker.checkIfUpperBoundGreaterThanLowerBound(state, lowerBound, upperBound);
        Checker.checkIfNumberAndGreaterZero(state, interval);
        Checker.checkIfNumberAndGreaterZero(state, scale, shape);
        Checker.checkIfValidColor(state, color);

        ParetoDistribution pd = new ParetoDistribution(scale.toJDoubleValue(state), shape.toJDoubleValue(state));


        /** The valueList is the list of every pair of coordinates [x,y] that the graph consists of.
         *  It is filled by iteratively increasing the variable 'counter' (x), and calculating the density for every new value of 'counter' (y).
         */
        List<List<Double>> valueList = new ArrayList<>();
        for (double counter = lowerBound.toJDoubleValue(state); counter < upperBound.toJDoubleValue(state); counter += interval.toJDoubleValue(state)) {
            valueList.add(new ArrayList<Double>(Arrays.asList(counter, pd.density(counter))));
        }

        return ConnectJFreeChart.getInstance().addListGraph((Canvas) canvas, valueList, "Probability Density Function (shape: " + shape.toString() + ", scale: " + scale.toString(), Defaults.createColorScheme(color, state), false);
    }
}
