package org.randoom.setlx.functions;

import org.apache.commons.math3.distribution.GammaDistribution;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Checker;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * stat_gammaCDF(x, p, b):
 *                  Computes the cumulative gamma distribution with mean given parameters 'p' and 'b'.
 */
public class PD_stat_gammaCDF extends PreDefinedProcedure {

    private final static ParameterDefinition X     = createParameter("x");
    private final static ParameterDefinition P     = createParameter("p");
    private final static ParameterDefinition B     = createParameter("b");

    /** Definition of the PreDefinedProcedure 'stat_gammaCDF' */
    public static final PreDefinedProcedure DEFINITION = new PD_stat_gammaCDF();

    private PD_stat_gammaCDF() {
        super();
        addParameter(X);
        addParameter(P);
        addParameter(B);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value x     = args.get(X);
        final Value p     = args.get(P);
        final Value b     = args.get(B);

        Checker.checkIfNumber(state, x);
        Checker.checkIfNumberAndGreaterZero(state, p, b);

        GammaDistribution gd = new GammaDistribution(p.toJDoubleValue(state), b.toJDoubleValue(state));
        return SetlDouble.valueOf(gd.cumulativeProbability(x.toJDoubleValue(state)));
    }
}
