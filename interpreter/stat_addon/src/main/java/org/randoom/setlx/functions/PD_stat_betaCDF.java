package org.randoom.setlx.functions;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Checker;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * Created on 02.03.17.
 * stat_betaCDF(x, alpha, beta):
 *      Implements the BetaCDF distribution. alpha,beta > 0
 */

public class PD_stat_betaCDF extends PreDefinedProcedure {

    private final static ParameterDefinition X   = createParameter("x");
    private final static ParameterDefinition ALPHA   = createParameter("alpha");
    private final static ParameterDefinition BETA    = createParameter("beta");

    /** Definition of the PreDefinedProcedure 'stat_betaCDF' */
    public final static PreDefinedProcedure DEFINITION = new PD_stat_betaCDF();

    private PD_stat_betaCDF() {
        super();
        addParameter(X);
        addParameter(ALPHA);
        addParameter(BETA);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value x       = args.get(X);
        final Value alpha   = args.get(ALPHA);
        final Value beta    = args.get(BETA);

        Checker.checkIfNumberAndGreaterOrEqualZero(state, x);
        Checker.checkIfNumberAndGreaterZero(state, alpha, beta);

        BetaDistribution bd = new BetaDistribution(alpha.toJDoubleValue(state), beta.toJDoubleValue(state));
        return SetlDouble.valueOf(bd.cumulativeProbability(x.toJDoubleValue(state)));
    }
}
