package org.randoom.setlx.functions;

import org.apache.commons.math3.distribution.WeibullDistribution;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Checker;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * Created on 1/23/17.
 * stat_weibullCDF(x, shape, scale):
 *      Computes the Lower Cumulative Distribution of the Weibull Distribution.
 */
public class PD_stat_weibullCDF extends PreDefinedProcedure {

    private final static ParameterDefinition X     = createParameter("x");
    private final static ParameterDefinition SHAPE = createParameter("shape");
    private final static ParameterDefinition SCALE = createParameter("scale");

    /** Definition of the PreDefinedProcedure 'stat_weibullCDF' */
    public final static PreDefinedProcedure DEFINITION = new PD_stat_weibullCDF();

    private PD_stat_weibullCDF() {
        super();
        addParameter(X);
        addParameter(SHAPE);
        addParameter(SCALE);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value x       = args.get(X);
        final Value shape   = args.get(SHAPE);
        final Value scale   = args.get(SCALE);

        Checker.checkIfNumberAndGreaterZero(state, x, shape, scale);

        WeibullDistribution wdcdf = new WeibullDistribution(shape.toJDoubleValue(state), scale.toJDoubleValue(state));
        return SetlDouble.valueOf(wdcdf.cumulativeProbability(x.toJDoubleValue(state)));
    }
}
