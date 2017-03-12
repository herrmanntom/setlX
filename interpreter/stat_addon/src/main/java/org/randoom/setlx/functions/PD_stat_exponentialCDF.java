package org.randoom.setlx.functions;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Checker;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * stat_exponentialCDF(x, l):
 *                  Computes the cumulative exponential distribution with given parameter 'l'.
 */
public class PD_stat_exponentialCDF extends PreDefinedProcedure {

    private final static ParameterDefinition X = createParameter("x");
    private final static ParameterDefinition L = createParameter("l");

    /** Definition of the PreDefinedProcedure 'stat_chiSquaredCDF' */
    public final static PreDefinedProcedure DEFINITION = new PD_stat_exponentialCDF();

    private PD_stat_exponentialCDF() {
        super();
        addParameter(X);
        addParameter(L);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value x = args.get(X);
        final Value l = args.get(L);

        Checker.checkIfNumber(state, x);
        Checker.checkIfNumberAndGreaterZero(state, l);

        ExponentialDistribution ed = new ExponentialDistribution(l.toJDoubleValue(state));
        return SetlDouble.valueOf(ed.cumulativeProbability(x.toJDoubleValue(state)));
    }
}
