package org.randoom.setlx.functions;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Checker;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * stat_studentCDF(x, nu):
 *                  Computes the cumulative normal distribution with mean 'nu' degrees of freedom.
 */
public class PD_stat_studentCDF extends PreDefinedProcedure {

    private final static ParameterDefinition X     = createParameter("x");
    private final static ParameterDefinition NU    = createParameter("nu");

    /** Definition of the PreDefinedProcedure 'stat_studentCDF' */
    public static final PreDefinedProcedure DEFINITION = new PD_stat_studentCDF();

    private PD_stat_studentCDF() {
        super();
        addParameter(X);
        addParameter(NU);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value x     = args.get(X);
        final Value nu    = args.get(NU);

        Checker.checkIfNumber(state, x);
        Checker.checkIfNaturalNumberAndGreaterZero(state, nu);

        TDistribution td = new TDistribution(nu.toJDoubleValue(state));
        return SetlDouble.valueOf(td.cumulativeProbability(x.toJDoubleValue(state)));
    }
}
