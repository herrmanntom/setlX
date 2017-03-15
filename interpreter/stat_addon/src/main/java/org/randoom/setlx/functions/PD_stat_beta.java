package org.randoom.setlx.functions;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Checker;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * Created on 02.03.17.
 * stat_beta(x, alpha, beta):
 *      Implements the Beta distribution. x != 0, x != 1; alpha,beta > 0
 */

public class PD_stat_beta extends PreDefinedProcedure {

    private final static ParameterDefinition X   = createParameter("x");
    private final static ParameterDefinition ALPHA   = createParameter("alpha");
    private final static ParameterDefinition BETA    = createParameter("beta");

    /** Definition of the PreDefinedProcedure 'stat_beta' */
    public final static PreDefinedProcedure DEFINITION = new PD_stat_beta();

    private PD_stat_beta() {
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


        Checker.checkIfNumberAndGreaterZero(state, alpha, beta);

        if (alpha.toJDoubleValue(state) < 1 || beta.toJDoubleValue(state) < 1) {
            if (x.toJDoubleValue(state) == 0 || x.toJDoubleValue(state) == 1) {
                throw new IncompatibleTypeException("The beta distribution is not defined for x == 0 and x == 1 if one of the parameters is less than 1.");
            }
        } else {
            Checker.checkIfNumber(state, x);
        }

        BetaDistribution bd = new BetaDistribution(alpha.toJDoubleValue(state), beta.toJDoubleValue(state));
        return SetlDouble.valueOf(bd.density(x.toJDoubleValue(state)));
    }
}
