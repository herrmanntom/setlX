package org.randoom.setlx.functions;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.plot.types.Canvas;
import org.randoom.setlx.plot.utilities.ConnectJFreeChart;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Checker;
import org.randoom.setlx.utilities.Defaults;
import org.randoom.setlx.utilities.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * stat_normalCDF_plot(mu, sigma):
 *                  Plots the cumulative distribution function for chiSquared distributions with 'k' degrees of freedom.
 */
public class PD_stat_chiSquaredCDF_plot extends PreDefinedProcedure {

    private final static ParameterDefinition K           = createParameter("k");
    private final static ParameterDefinition LOWER_BOUND = createOptionalParameter("lowerBound", SetlDouble.ZERO);
    private final static ParameterDefinition INTERVAL    = createOptionalParameter("interval", SetlDouble.DEFAULT_INTERVAL);
    private final static ParameterDefinition UPPER_BOUND = createOptionalParameter("upperBound", SetlDouble.TEN);

    public final static PreDefinedProcedure DEFINITION = new PD_stat_chiSquaredCDF_plot();

    private PD_stat_chiSquaredCDF_plot() {
        super();
        addParameter(K);
        addParameter(LOWER_BOUND);
        addParameter(INTERVAL);
        addParameter(UPPER_BOUND);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value k          = args.get(K);
        final Value lowerBound = args.get(LOWER_BOUND);
        final Value interval   = args.get(INTERVAL);
        final Value upperBound = args.get(UPPER_BOUND);

        Checker.checkIfNaturalNumber(state, k);

        ChiSquaredDistribution csd = new ChiSquaredDistribution(k.toJDoubleValue(state));

        Canvas canvas = ConnectJFreeChart.getInstance().createCanvas();

        List<List<Double>> valueList = new ArrayList<>();
        for (double counter = lowerBound.toJDoubleValue(state); counter < upperBound.toJDoubleValue(state); counter += interval.toJDoubleValue(state)) {
            valueList.add(new ArrayList<Double>(Arrays.asList(counter, csd.cumulativeProbability(counter))));
        }

        return ConnectJFreeChart.getInstance().addListGraph(canvas, valueList, "Cumulative ChiSquared Distribution Function (" + k.toString() + " degree(s) of freedom)", Defaults.DEFAULT_COLOR_SCHEME, false);
    }
}
