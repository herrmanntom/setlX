package org.randoom.setlx.functions;

import org.apache.commons.math3.distribution.LevyDistribution;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Checker;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * stat_levy(x, mu, scale):
 *      Computes the levy distribution with mu (location) and scale.
 */
public class PD_stat_levy extends PreDefinedProcedure {

    private final static ParameterDefinition X     = createParameter("x");
    private final static ParameterDefinition MU    = createParameter("mu");
    private final static ParameterDefinition SCALE = createParameter("scale");

    /** Definition of the PreDefinedProcedure 'stat_levy' */
    public final static PreDefinedProcedure DEFINITION = new PD_stat_levy();

    private PD_stat_levy() {
        super();
        addParameter(X);
        addParameter(MU);
        addParameter(SCALE);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value x     = args.get(X);
        final Value mu    = args.get(MU);
        final Value scale = args.get(SCALE);

        if (!(x.toJDoubleValue(state) > mu.toJDoubleValue(state))) {
            throw new IncompatibleTypeException("stat_levy(x, mu, scale). x has to be greater than mu.");
        }

        Checker.checkIfNumber(state, x, mu);
        Checker.checkIfNumberAndGreaterZero(state, scale);

        LevyDistribution ld = new LevyDistribution(mu.toJDoubleValue(state), scale.toJDoubleValue(state));
        return SetlDouble.valueOf(ld.density(x.toJDoubleValue(state)));
    }
}