package org.randoom.setlx.functions;

import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Checker;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * stat_logNormal(x, mu, sigma):
 *                  Computes the log-normal distribution with mean 'mu' and standard deviation 'sigma'.
 */
public class PD_stat_logNormal extends PreDefinedProcedure {
    private final static ParameterDefinition X     = createParameter("x");
    private final static ParameterDefinition MU    = createParameter("mu");
    private final static ParameterDefinition SIGMA = createParameter("sigma");

    /** Definition of the PreDefinedProcedure 'stat_logNormal' */
    public final static PreDefinedProcedure DEFINITION = new PD_stat_logNormal();

    private PD_stat_logNormal() {
        super();
        addParameter(X);
        addParameter(MU);
        addParameter(SIGMA);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value x     = args.get(X);
        final Value mu    = args.get(MU);
        final Value sigma = args.get(SIGMA);

        Checker.checkIfNumber(state, x, mu);
        Checker.checkIfNumberAndGreaterZero(state, sigma);

        LogNormalDistribution lnd = new LogNormalDistribution(mu.toJDoubleValue(state), Math.pow(sigma.toJDoubleValue(state), 2));
        return SetlDouble.valueOf(lnd.density(x.toJDoubleValue(state)));
    }
}
