package org.randoom.setlx.functions;

import org.apache.commons.math3.distribution.ParetoDistribution;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Checker;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * stat_paretoCDF(x, scale, shape):
 *      Computes the paretoCDF distribution with scale and shape.
 */
public class PD_stat_paretoCDF extends PreDefinedProcedure {

    private final static ParameterDefinition X     = createParameter("x");
    private final static ParameterDefinition SCALE = createParameter("scale");
    private final static ParameterDefinition SHAPE = createParameter("shape");

    /** Definition of the PreDefinedProcedure 'stat_paretoCDF' */
    public final static PreDefinedProcedure DEFINITION = new PD_stat_paretoCDF();

    private PD_stat_paretoCDF() {
        super();
        addParameter(X);
        addParameter(SCALE);
        addParameter(SHAPE);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value x     = args.get(X);
        final Value scale = args.get(SCALE);
        final Value shape = args.get(SHAPE);

        Checker.checkIfNumber(state, x);
        Checker.checkIfNumberAndGreaterZero(state, scale, shape);

        ParetoDistribution pd = new ParetoDistribution(scale.toJDoubleValue(state), shape.toJDoubleValue(state));
        return SetlDouble.valueOf(pd.cumulativeProbability(x.toJDoubleValue(state)));
    }
}