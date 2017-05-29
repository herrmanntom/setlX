package org.randoom.setlx.functions;

import org.apache.commons.math3.distribution.LogNormalDistribution;
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
 * stat_normal_plot(mu, sigma, canvas):
 *                  Plots the probability density function for log-normal distributions with given mean 'mu' and standard deviation 'sigma' on a given canvas.
 */
public class PD_stat_logNormal_plot extends PreDefinedProcedure {

    private final static ParameterDefinition CANVAS      = createParameter("canvas");
    private final static ParameterDefinition MU          = createParameter("mu");
    private final static ParameterDefinition SIGMA       = createParameter("sigma");
    private final static ParameterDefinition COLOR       = createOptionalParameter("color", new SetlString("DEFAULT_COLOR"));
    private final static ParameterDefinition LOWER_BOUND = createOptionalParameter("lowerBound", Defaults.createSetlDoubleValue(0.0));
    private final static ParameterDefinition INTERVAL    = createOptionalParameter("interval", Defaults.getDefaultPlotInterval());
    private final static ParameterDefinition UPPER_BOUND = createOptionalParameter("upperBound", Defaults.createSetlDoubleValue(5.0));

    /** Definition of the PreDefinedProcedure 'stat_logNormal_plot' */
    public final static PreDefinedProcedure DEFINITION = new PD_stat_logNormal_plot();

    private PD_stat_logNormal_plot() {
        super();
        addParameter(CANVAS);
        addParameter(MU);
        addParameter(SIGMA);
        addParameter(COLOR);
        addParameter(LOWER_BOUND);
        addParameter(INTERVAL);
        addParameter(UPPER_BOUND);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value canvas     = args.get(CANVAS);
        final Value mu         = args.get(MU);
        final Value sigma      = args.get(SIGMA);
        final Value color      = args.get(COLOR);
        final Value lowerBound = args.get(LOWER_BOUND);
        final Value interval   = args.get(INTERVAL);
        final Value upperBound = args.get(UPPER_BOUND);

        Checker.checkIfCanvas(state, canvas);
        Checker.checkIfNumber(state, mu, lowerBound, upperBound);
        Checker.checkIfUpperBoundGreaterThanLowerBound(state, lowerBound, upperBound);
        Checker.checkIfNumberAndGreaterZero(state, interval);
        Checker.checkIfNumberAndGreaterZero(state, sigma);
        Checker.checkIfValidColor(state, color);

        LogNormalDistribution lnd = new LogNormalDistribution(mu.toJDoubleValue(state), Math.pow(sigma.toJDoubleValue(state), 2));

        /** The valueList is the list of every pair of coordinates [x,y] that the graph consists of.
         *  It is filled by iteratively increasing the variable 'counter' (x), and calculating the density for every new value of 'counter' (y).
         */
        List<List<Double>> valueList = new ArrayList<>();
        for (double counter = lowerBound.toJDoubleValue(state); counter < upperBound.toJDoubleValue(state); counter += interval.toJDoubleValue(state)) {
            valueList.add(new ArrayList<Double>(Arrays.asList(counter, lnd.density(counter))));
        }

        return ConnectJFreeChart.getInstance().addListGraph((Canvas) canvas, valueList, "Probability Density Function (mean: " + mu.toString() + ", standard deviation: " + sigma.toString(), Defaults.createColorScheme(color, state), false);
    }
}
